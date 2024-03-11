package com.wenku.documents_wenku.job;

import com.wenku.documents_wenku.constant.RedisConstant;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 定时更新用户点赞和浏览量
 *
 * 频率	2h
 *
 * @author gaffey
 */

@Component
@Slf4j
public class UpdateLikeAndReadJob {
	@Resource
	private RedisTemplate<String,Object> redisTemplate;

	@Resource
	private RedissonClient redissonClient;

	@Resource
	private DocumentService documentService;

	@Scheduled(cron = "0 0 * * * *")  // 每小时执行一次

	public void updateLikeAndReadCount(){
		RLock lock = redissonClient.getLock("wenku:updateLikeAndReadCount:lock");
		log.info(Thread.currentThread().getId() + " ---- 获得更新点赞和浏览量锁");
		log.info(Thread.currentThread().getId() + " ---- 开始更新任务" + new Date());
		try{
			Set keys = redisTemplate.keys(RedisConstant.DOCUMENT_COUNT_REDIS + "*");
			List<String > list = new ArrayList<>(keys);
			System.out.println(list);
			List<Long> documentId = new ArrayList<>();
			for(String str : list){
				documentId.add(Long.valueOf(str.substring(RedisConstant.DOCUMENT_COUNT_REDIS.length(),str.length())));
			}
			System.out.println(documentId);
			for (Long id : documentId){
				Document document = documentService.searchDocumentById(id);
				if(document != null){
					System.out.println(document);
					long likes = document.getLikes();
					long browser = document.getBrowser();
					System.out.println("Before   浏览："+browser +"点赞"+ likes);
					Object readcount = redisTemplate.opsForHash().get(RedisConstant.DOCUMENT_COUNT_REDIS + id, "readcount");
					Object likecount = redisTemplate.opsForHash().get(RedisConstant.DOCUMENT_COUNT_REDIS + id, "likecount");
					if(readcount != null){
						browser += (Integer) readcount;
					}
					if(likecount != null){
						likes += (Integer)(likecount);
					}
					boolean update = false;
					try{
						update = documentService.updateLandB(likes, browser, id);
					}catch(Exception e){
					    log.error("updateLikeAndReadCount时更新数据库失败" + e);
					}
					if(update && redisTemplate.hasKey(RedisConstant.DOCUMENT_COUNT_REDIS + id)){
						//更新成功
						redisTemplate.delete(RedisConstant.DOCUMENT_COUNT_REDIS + id);
					}
				}
			}
		}catch(Exception e){
		    log.error("updateLikeAndReadCount ---- "+e);
			log.info(Thread.currentThread().getId() + " ---- 更新任务执行结束" + new Date());
		}finally {
			if (lock.isHeldByCurrentThread()){
				lock.unlock();
				log.info(Thread.currentThread().getId() + " ---- 更新任务执行结束" + new Date());
				log.info(Thread.currentThread().getId()+" ---- 释放缓存任务锁" + new Date());
			}
		}
	}

}
