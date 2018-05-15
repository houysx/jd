package cn.it.lucene.serch;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SerchIndex {

	/**
	 * 需求:根据关键词查询索引库
	 * 
	 */
	@Test
	public void queryIndex() throws Exception {
		// 指定搜索关键词
		String qName = "宝典";
		// 创建查询解析器,对查询关键词进行分词
		// 参数1:指定从那个域字段的索引单词进行匹配
		// 参数2:指定搜索是使用何种分词器(在中文搜索环境中,必须保证搜索分词器和创建索引库分词器必须保持一致)

		// *1.1,单字段搜索匹配查询解析器
		// QueryParser qParser = new QueryParser("title", new IKAnalyzer());

		// *1.2,多字段匹配查询解析器
		MultiFieldQueryParser mParser = new MultiFieldQueryParser(new String[] {
				"title", "desc", "content" }, new IKAnalyzer());

		// 解析查询关键词,返回解析后保存对象
		Query query = mParser.parse(qName);
		
		//调用查询索引库方法
		this.excuteAndPrintResult(query);

	}
	
	/**
	 * 
	 * 需求: 2.1 termQuery查询
	 * 特点: 查询关键词已经是最小分词单元,不需要分词
	 * @throws Exception 
	 */
	@Test
	public void testTermQuery() throws Exception{
		//指定查询关键词
		String qName = "宝典";
		//创建termQuery对象
		TermQuery query = new TermQuery(new Term("title", qName));
		//调用查询索引库方法
		this.excuteAndPrintResult(query);
		
	}
	
	/**
	 * 
	 * 需求: 2.2WildcardQuery查询
	 * 特点: 模糊检索
	 * *匹配所有 
	 */
	@Test
	public void testWildcardQuery() throws Exception{
		//指定查询关键词
		String qName = "搜*";
		//创建termQuery对象
		WildcardQuery query = new WildcardQuery(new Term("title", qName));
		//调用查询索引库方法
		this.excuteAndPrintResult(query);
		
	}
	
	
	
	/**
	 * 2.3 需求: fuzzyQuery查询
	 * 特点: 查询关键词已经是最小分词单元,不需要分词
	 * @throws Exception 
	 * 相似度查询算法:
	 * 最多经过2次变化,能变回源单词,就长得像.
	 * QucXne--lucXne--lucene
	 */
	@Test
	public void testFuzzyQuery() throws Exception{
		//指定查询关键词
		String qName = "QucXne";
		//创建fuzzyQuery对象
		FuzzyQuery query = new FuzzyQuery(new Term("title", qName));
		//调用查询索引库方法
		this.excuteAndPrintResult(query);
		
	}
	
	/**
	 * 需求: 2.4、NumericRangeQuery查询
	 * 特点: 查询关键词已经是最小分词单元,不需要分词
	 * 相似度查询算法:
	 * 最多经过2次变化,能变回源单词,就长得像.
	 * QucXne--lucXne--lucene
	 */
	@Test
	public void testNumericRangeQuery() throws Exception{
		//NumericRangeQuery范围查询
		//参数1: 指定搜索域字段
		//参数2: 指定搜索起始位置(角标)
		//参数3: 指定搜索结束位置(角标)
		//参数4: 左边开[不包含:false](闭[包含:true])
		//参数5: 右边开[不包含:false](闭[包含:true])
		NumericRangeQuery<Long> query = NumericRangeQuery.newLongRange("id", 5L, 15L, false, true);
		//调用查询索引库方法
		this.excuteAndPrintResult(query);
		
	}
	
	
	
	/**
	 * 需求: 2.5、booleanQuery组合查询
	 * 特点: 查询关键词已经是最小分词单元,不需要分词
	 * @throws Exception 
	 */
	@Test
	public void testBooleanQuery() throws Exception{
		//创建范围查询
		NumericRangeQuery<Long> query1 = NumericRangeQuery.newLongRange("id", 5L, 15L, false, true);
		
		//创建查询所有
		MatchAllDocsQuery query2 = new MatchAllDocsQuery();
		
		//创建组合查询对象
		//组合query1,和 query2,求交集
		BooleanQuery query = new BooleanQuery();
		//求补集
		query.add(query1, Occur.MUST_NOT);
		query.add(query2, Occur.MUST);
		
		//调用查询索引库方法
		this.excuteAndPrintResult(query);
		
	}
	


	
	public void excuteAndPrintResult(Query query) throws Exception {
		/**
		 * 将检索内容转成query对象 
		 * 1.1模拟检索关键词 
		 * 1.2创建检索解析器 要和索引创建的解析器保持一致 
		 * 1.3指定对哪个字段进行检索 
		 * 参数1： 解析的字段
		 * 参数2:检索使用的分词器 
		 * 1.4 获取分词后的结果
		 */

		/**
		 * 2.1指定索引所在目录 
		 * 2.2 
		 * 2.3创建检索对象
		 */

		Directory directory = FSDirectory.open(new File("D:\\workplace2\\luceneIndex"));
		IndexReader indexReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		/**
		 * 3.1通过search方法检索
		 */
		TopDocs dosc = indexSearcher.search(query, 10);
		/**
		 * 4解析检索结果 
		 * 4.1 获取最高命中率 
		 * 4.2 遍历检索结果 
		 * 4.3获取文档id 
		 * 4.4 通过文档id获取文档id：scoreDoc.doc
		 * 4.5获取文档字段值
		 */
		int totalHits = dosc.totalHits;
		System.out.println("命中率:" + totalHits);
		ScoreDoc[] scoreDocs = dosc.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			float score = scoreDoc.score;
			System.out.println("得分:" + score);
			// 获取文档id
			int doc = scoreDoc.doc;
			System.out.println("文档id:" + doc);
			// 通过文档id获取文档
			Document document = indexSearcher.doc(doc);
			String id = document.get("id");
			System.out.println("文档字段id：" + id);
			String title = document.get("title");
			System.out.println("文档标题：" + title);
			String desc = document.get("desc");
			System.out.println("文档描述：" + desc);
			String content = document.get("content");
			System.out.println("文档内容：" + content);
			System.out.println("=========================================");

		}

	};

}
