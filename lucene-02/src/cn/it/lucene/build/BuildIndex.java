package cn.it.lucene.build;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class BuildIndex {

	
		/**
		 * 需求:lucene入门案例 ==>创建索引库
		 * 
		 * @throws Exception
		 */
		@Test
		public void addIndex() throws Exception {
			// 指定索引文件存储位置
			String indexPath = "F:\\indexs";
			// 创建目录对象,关联索引库存储位置
			FSDirectory directory = FSDirectory.open(new File(indexPath));

			// 创建分词器对象
			// 1,基本分词器
			// Analyzer analyzer = new StandardAnalyzer();

			// 2,cjk分词器
			// Analyzer analyzer = new CJKAnalyzer();

			// 3,聪明的中国人分词器
			// Analyzer analyzer = new SmartChineseAnalyzer();

			// 4,ik分词器
			Analyzer analyzer = new IKAnalyzer();

			// 创建核心对象的配置对象
			// 参数1: 指定lucene版本
			// 参数2: 指定创建索引库使用的分词器
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_3,
					analyzer);

			// 创建写入索引库数据核心对象
			IndexWriter indexWriter = new IndexWriter(directory, iwc);

			// 网页
			// 分析网页特点:id,title,desc,content,url
			// 文件:
			// 特点:id,title,desc,content,url
			// 数据库:文章表
			// id,title,desc,content

			// 循环创建30个文档
			for (int i = 0; i < 30; i++) {
				// 模拟文档对象
				// 添加数据库站内搜索数据
				// 1,查询数据库文章表数据库(略)
				// 2,创建文档对象封装数据库数据
				Document doc = new Document();

				doc.add(new LongField("id", i, Store.YES));

				TextField field = new TextField("title",
						"高富帅唐僧,在搜索学习java,lucene经典教程", Store.YES);

				if (i == 22) {
					// 设置得分
					field.setBoost(10000);
				}

				// 封装title
				// TextField:索引域字段类型
				// 特点:
				// 1,必须被分词器解析,一定会分词
				// 2,索引
				// 3,存储Store.YES
				doc.add(field);

				// 封装描述字段
				doc.add(new TextField(
						"desc",
						"全文检索系统是按照全文检索理论建立起来的用于提供全文检索服务的软件系统,Lucene并不是现成的搜索引擎产品，但可以用来制作搜索引擎产品",
						Store.YES));

				// 封装内容
				doc.add(new TextField("content",
						"Lucene和搜索引擎不同，Lucene是一套用java或其它语言写的全文检索的工具包....", Store.NO));

				// 使用核心对象写入索引库
				indexWriter.addDocument(doc);

			}

			// 提交
			indexWriter.commit();

		}

		/**
		 * 需求:lucene入门案例 ==>修改索引库
		 * 修改流程:
		 * 1,先根据关键词查询
		 * 2,再把查询所有文档全部删除
		 * 3,在把现在的文档添加
		 * 查询title域包含搜索关键词所有文档(30个文档全部包含),再删除,再把当前文档进行添加
		 */
		@Test
		public void updateIndex() throws Exception {
			// 指定索引文件存储位置
			String indexPath = "F:\\indexs";
			// 创建目录对象,关联索引库存储位置
			FSDirectory directory = FSDirectory.open(new File(indexPath));
			// 4,ik分词器
			Analyzer analyzer = new IKAnalyzer();
			// 创建核心对象的配置对象
			// 参数1: 指定lucene版本
			// 参数2: 指定创建索引库使用的分词器
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_3,
					analyzer);
			// 创建写入索引库数据核心对象
			IndexWriter indexWriter = new IndexWriter(directory, iwc);

			Document doc = new Document();
			doc.add(new StringField("id", "10101111111", Store.YES));
			doc.add(new TextField("title", "高富帅唐僧,在搜索学习java,lucene经典教程",
					Store.YES));

			// 封装描述字段
			doc.add(new TextField(
					"desc",
					"全文检索系统是按照全文检索理论建立起来的用于提供全文检索服务的软件系统,Lucene并不是现成的搜索引擎产品，但可以用来制作搜索引擎产品",
					Store.YES));

			// 封装内容
			doc.add(new TextField("content",
					"Lucene和搜索引擎不同，Lucene是一套用java或其它语言写的全文检索的工具包....", Store.NO));

			// 修改
			// 修改流程:
			//1,先根据关键词查询
			//2,再把查询所有文档全部删除
			//3,在把现在的文档添加
			//查询title域包含搜索关键词所有文档(30个文档全部包含),再删除,再把当前文档进行添加
			indexWriter.updateDocument(new Term("title","搜索"), doc);

			// 提交
			indexWriter.commit();

		}
		
		
		@Test
		public void deleteIndex() throws Exception {
			// 指定索引文件存储位置
			String indexPath = "F:\\indexs";
			// 创建目录对象,关联索引库存储位置
			FSDirectory directory = FSDirectory.open(new File(indexPath));
			// 4,ik分词器
			Analyzer analyzer = new IKAnalyzer();
			// 创建核心对象的配置对象
			// 参数1: 指定lucene版本
			// 参数2: 指定创建索引库使用的分词器
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_3,
					analyzer);
			// 创建写入索引库数据核心对象
			IndexWriter indexWriter = new IndexWriter(directory, iwc);
			//分词查询删除
			//indexWriter.deleteDocuments(query);
			//不分词,直接查询删除
			//查询title域字段包含搜索的文档,全部删除
			indexWriter.deleteDocuments(new Term("title","搜索"));

			// 提交
			indexWriter.commit();

		}

	}

