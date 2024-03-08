package com.wenku.documents_wenku.service.impl;

import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.service.DocumentService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@SpringBootTest
public class InsertTest {

	@Resource
	private DocumentService documentService;
	@Resource
	Random random;


	private ExecutorService executorService =
			new ThreadPoolExecutor(20,
					1000,
					10000,
					TimeUnit.MINUTES,
					new ArrayBlockingQueue<>(10000));
	/**
	 * 并发批量插入数据
	 */
	@Test
	public void doConcurrencyInsertUsers() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		// 分十组
		int batchSize = 100;
		int j = 0;
		List<CompletableFuture<Void>> futureList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			List<Document> documentList = new ArrayList<>();
			while (true) {
				j++;
				Document document = new Document();
				document.setDocumentName("1-批量插入测试文档");
				document.setCategory("图书");
				document.setUploadUserId(1L);
				document.setDucomentUrl("http://document.com");
				document.setTags("[Tags]");
				document.setLikes(random.nextLong(i));
				documentList.add(document);
				if (j % batchSize == 0) {
					break;
				}
			}
			// 异步执行
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				System.out.println("threadName: " + Thread.currentThread().getName());
				documentService.saveBatch(documentList, batchSize);
			}, executorService);
			futureList.add(future);
		}
		CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
		// 20 秒 10 万条s
		stopWatch.stop();
		System.out.println(stopWatch.getTotalTimeMillis());
	}

}
