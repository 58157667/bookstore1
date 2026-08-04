package com.example.demo;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qiniu.storage.BucketManager;
import com.qiniu.storage.model.FileInfo;
@Service
public class BookSyncService {
    @Resource
    private BucketManager bucketManager;
    @Resource
    private QiniuProperties qiniuProperties;
    @Resource
    private BookDao bookDao;
    @Resource
    private CategoryDao categoryDao;

    // 同步七牛图书，默认分类id=1（计算机）
    @Transactional(rollbackFor = Exception.class)
    public void syncQiniuBook() {
        String bucket = qiniuProperties.getBucketName();
        String domain = qiniuProperties.getDomain();
        String pdfPrefix = qiniuProperties.getPdfFolder();
        String coverPrefix = qiniuProperties.getCoverFolder();
        Map<String, FileInfo> pdfMap = listSafeFileMap(bucket, pdfPrefix, ".pdf");
        Map<String, FileInfo> coverMap = listSafeFileMap(bucket, ".png", coverPrefix);
        // 默认分类：计算机 id=1
        Long defaultCategoryId = 1L;

        for (String fileName : pdfMap.keySet()) {
            FileInfo pdfFile = pdfMap.get(fileName);
            FileInfo coverFile = coverMap.get(fileName);
            if (coverFile == null) continue;
            if (bookDao.existsByTitle(fileName)) {
                System.out.println("已存在，跳过：" + fileName);
                continue;
            }
            Book book = new Book();
            book.setTitle(fileName);
            String pdfEncodeKey = customUrlEncode(pdfFile.key);
            String pdfFullUrl = domain + "/" + pdfEncodeKey;
            book.setBookFileUrl(pdfFullUrl);
            book.setBookFileUrlPublicId(pdfFile.key);
            String coverEncodeKey = customUrlEncode(coverFile.key);
            String coverFullUrl = domain + "/" + coverEncodeKey;
            book.setCoverImageUrl(coverFullUrl);
            book.setCoverImagePublicId(coverFile.key);
            book.setDescription("七牛云批量导入图书");
            book.setPublisher("未标注");
            book.setPublicationDate(null);
            book.setAuthorId(null);
            book.setCategoryId(defaultCategoryId);
            bookDao.insertBook(book);
            System.out.println("入库成功：" + fileName);
        }
    }

    private String customUrlEncode(String key) {
        String encodeStr = URLEncoder.encode(key, StandardCharsets.UTF_8);
        encodeStr = encodeStr.replace("%2F", "/");
        encodeStr = encodeStr.replace("+", "%20");
        return encodeStr;
    }
    private Map<String, FileInfo> listSafeFileMap(String bucket, String prefix, String suffix) {
        Map<String, FileInfo> result = new HashMap<>();
        BucketManager.FileListIterator iterator = bucketManager.createFileListIterator(bucket, prefix, 1000, "");
        while (iterator.hasNext()) {
            FileInfo[] fileArr = iterator.next();
            for (FileInfo fileInfo : fileArr) {
                String key = fileInfo.key;
                if (key == null || key.isBlank()) continue;
                if (!key.endsWith(suffix)) continue;
                String pureName = key.replace(prefix, "").replace(suffix, "");
                result.put(pureName, fileInfo);
            }
        }
        return result;
    }

    // 每页固定10条
    private final int PAGE_SIZE = 10;
    /**
     * 分页全部图书 + 支持分类筛选
     */
    public Map<String, Object> getBookList(Integer page, Long categoryId) {
        int pageNum = page == null ? 1 : page;
        List<Book> list = bookDao.listAllBook(pageNum, PAGE_SIZE, categoryId);
        int total = bookDao.countAll(categoryId);
        int totalPage = (total + PAGE_SIZE - 1) / PAGE_SIZE;
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("current", pageNum);
        map.put("totalPage", totalPage);
        map.put("keyword", "");
        map.put("selectCategoryId", categoryId);
        // 传入全部分类，页面下拉框渲染
        map.put("categoryList", categoryDao.listAll());
        return map;
    }
    /**
     * 书名模糊搜索 + 分类筛选
     */
    public Map<String, Object> searchBook(String keyword, Integer page, Long categoryId) {
        int pageNum = page == null ? 1 : page;
        List<Book> list = bookDao.searchBookByTitle(keyword, categoryId, pageNum, PAGE_SIZE);
        int total = bookDao.countSearch(keyword, categoryId);
        int totalPage = (total + PAGE_SIZE - 1) / PAGE_SIZE;
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("current", pageNum);
        map.put("totalPage", totalPage);
        map.put("keyword", keyword);
        map.put("selectCategoryId", categoryId);
        map.put("categoryList", categoryDao.listAll());
        return map;
    }
}