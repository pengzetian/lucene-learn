package com.lucene.learn.day01;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.nio.file.Paths;


/**
 * @author keven
 * @desc
 */
public class LuceneDay01 {
    
    
    
    private String ids[] = { "1", "2", "3" };
    private String citys[] = { "qingdao", "nanjing", "shanghai" };
    private String descs[] = { "Qingdao java is a beautiful city.",
            "Nanjing is  java a city of culture.", "Shanghai is java a bustling city. " };
    
    private Directory directory;
    
    
    @Test
    public void setUp() throws Exception{
        // 得到luceneIndex目录
        directory = FSDirectory.open(Paths.get("indexDir/"));
        // 得到索引
        IndexWriter writer = getWriter(directory);
        for (int i = 0; i < ids.length; i++) {
            // 创建文档
            Document doc = new Document();
            // 将id属性存入内存中
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("city", citys[i], Field.Store.YES));
            doc.add(new TextField("desc", descs[i], Field.Store.YES));
            // 添加文档
            writer.addDocument(doc);
        }
        writer.close();
    }
    
    
    public void testIndexWriter(Directory directory) throws Exception {
        IndexWriter writer = getWriter(directory);
        System.out.println("写入了" + writer.numDocs() + "个文档");
        writer.close();
    }
    
    @Test
    public  void search() throws Exception {
        
        String indexDir = "indexDir/";
        // 打开目录
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        // 进行读取
        IndexReader reader = DirectoryReader.open(dir);
        // 索引查询器
        IndexSearcher is = new IndexSearcher(reader);
        // 标准分词器
        Analyzer analyzer = new StandardAnalyzer();
        // 在哪查询，第一个参数为查询的Document，在Indexer中创建了
        QueryParser parser = new QueryParser("desc", analyzer);
        // 对字段进行解析后返回给查询
        Query query = parser.parse("java");
        long start = System.currentTimeMillis();
        // 开始查询，10代表前10条数据；返回一个文档
        TopDocs hits = is.search(query, 10);
        long end = System.currentTimeMillis();
        System.out.println("匹配 "  + " ，总共花费" + (end - start) + "毫秒" + "查询到"
                                   + hits.totalHits + "个记录");
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            // 根据文档的标识获取文档
            Document doc = is.doc(scoreDoc.doc);
            System.out.println(doc.get("fullPath"));
        }
        reader.close();
    }
    
    /**
     * 获取到 IndexWriter 实例
     *
     * @return
     */
    private IndexWriter getWriter(Directory directory) throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        return new IndexWriter(directory, indexWriterConfig);
    }
    
    
    
    @Test
    public void queryNumDoc() throws Exception {
        directory = FSDirectory.open(Paths.get("indexDir/"));
        // 通过reader 可以有效的获取到文档的数量
        IndexReader reader = DirectoryReader.open(directory);
        System.out.println("numDocs: "+ reader.numDocs());
        System.out.println("maxDocs:" +reader.maxDoc());
        System.out.println("deleteDocs:" + reader.numDeletedDocs());
    }
    
    @Test
    public void deleteDocument() throws Exception{
        directory = FSDirectory.open(Paths.get("indexDir/"));
        IndexWriter writer = getWriter(directory);
        //把id 为1 的document 删除掉，
        //参数是一个选项，可以是一个Query, 也可以是一个term, term是一个精确查找的值
        //此时删除的文档 并不会被完全删除，而是存储在一个回收站中，可以恢复
        writer.deleteDocuments(new Term("id","2"));
    }
    
    
}
