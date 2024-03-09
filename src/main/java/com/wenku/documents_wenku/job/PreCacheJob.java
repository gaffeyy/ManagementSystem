package com.wenku.documents_wenku.job;


import com.wenku.documents_wenku.constant.RedisConstant;
import com.wenku.documents_wenku.model.domain.Document;
import com.wenku.documents_wenku.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热项目
 *
 * @author GaffEy
 */

@Component
@Slf4j
public class PreCacheJob {


	@Resource
	private RedissonClient redissonClient;

	@Resource
	private DocumentService documentService;

	@Resource
	private RedisTemplate<String ,Object> redisTemplate;

	/**
	 * 缓存预热首页每日推荐
	 *
	 */

	@Scheduled(cron = "0 0 0 * * *")  // 秒 时 分 * * *
	public void doCacheRecommendDocument(){
		System.out.println("start");
		log.info("start schedule");
		RLock rLock = redissonClient.getLock("wenku:doCacheRecommendDocument:lock");
		try{
			List<Document> recomendList = new ArrayList<>();
			int size = 0;
			if(rLock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
				Long originSize = redisTemplate.opsForList().size(RedisConstant.RECOMEND_TOP_DOCUMENT);
				for (int i = 0;i < originSize;i++){
					redisTemplate.opsForList().leftPop(RedisConstant.RECOMEND_TOP_DOCUMENT);
				}
				log.info(Thread.currentThread().getId()+"get lock");
				recomendList = documentService.recommednDocument();
				size = recomendList.size();
			}
			try{
				for(int i = 0;i < size;i++){
					redisTemplate.opsForList().leftPush(RedisConstant.RECOMEND_TOP_DOCUMENT,recomendList.get(i));
				}
			}catch(Exception e){
			    log.error("Redis set key Error",e);
			}
		}catch(Exception e){
		    log.error("doCacheRecommendDocument Error",e);
		}finally {
			if (rLock.isHeldByCurrentThread()){
				rLock.unlock();
				log.info(Thread.currentThread().getId()+"unlocck");
			}
		}
	}
}
