package com.demo.microservices.controller;


import java.util.List; 
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.demo.microservices.dao.RentCntrMgntDAO;
import com.demo.microservices.dao.SampleUserDao;
import com.demo.microservices.model.Hello;
import com.demo.microservices.model.RentCntrList;
import com.demo.microservices.model.SampleUser;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class RentMgntController {
	private String msgTemplate = "%s 님 반갑습니다.";
	private final AtomicLong vistorCounter = new AtomicLong();

	@Autowired
	private SampleUserDao sampleUserDao;
	
	@Autowired
	private RentCntrMgntDAO rentCntrMgntDAO;

	@Value("${garage.product.server}")
	String productServer;
	
	@Value("${garage.product.port}")
	String productPort;
	

	
	@ApiOperation(value="전체 임대계약목록 정보 가져오기")
	@GetMapping(value="/RentCntrList/{custNo}")
	public ResponseEntity <List<RentCntrList>> getRentCntrListAll(@PathVariable String custNo) { 
		
		List<RentCntrList> list = null;
		try {
			log.info("Start db select");
			list = rentCntrMgntDAO.selectRentCntrAll(custNo);
		} catch (Exception e) {
			log.error("ERROR", e);
			throw new RuntimeException(e);
		}
		
		log.info("user counts :"+list.size());
		
		return new ResponseEntity<List<RentCntrList>> (list, HttpStatus.OK);
	}

	@ApiOperation(value="고객선호모델임대계약등록")
	@PostMapping(value="/RentCntrIn/custNo={custNo}&modelNo={modelNo}")
	public ResponseEntity <String> insertRentCntr(@PathVariable String custNo, @PathVariable String modelNo ) { 
		
		String msg = null;
		
		try {
			rentCntrMgntDAO.insertRentCntr(custNo, modelNo);
			rentCntrMgntDAO.deleteCustRegModel(custNo, modelNo);
		} catch (Exception e) {
			log.error("ERROR", e);
			throw new RuntimeException(e);
		}
		msg = "임대계약등록 성공";
		return new ResponseEntity<String> (msg, HttpStatus.OK);
	}
	
	@ApiOperation(value="임대계약다음단계처리")
	@PostMapping(value="/RentCntrNextStep/rentCntrNo={rentCntrNo}&progress={progress}")
	public ResponseEntity <String> updateRentCntrStep(@PathVariable String rentCntrNo, @PathVariable int progress ) { 
		
		String msg = null;
		char cd = 'A';
		progress = progress + 1;
		
		if(progress == 5) { cd = 'C'; }
		
		try {
			rentCntrMgntDAO.updateRentCntrStep(rentCntrNo, progress, cd);
		} catch (Exception e) {
			log.error("ERROR", e);
			throw new RuntimeException(e);
		}
		msg = "임대계약다음단계처리";
		return new ResponseEntity<String> (msg, HttpStatus.OK);
	}

//	@ApiOperation(value="사용자 목록 정보 가져오기")
//	@GetMapping(value="/users")
//	public ResponseEntity <List<SampleUser>> getUserAll() { 
//		
//		List<SampleUser> list = null;
//		try {
//			log.info("Start db select");
//			list = sampleUserDao.selectUserAll();
//		} catch (Exception e) {
//			log.error("ERROR", e);
//			throw new RuntimeException(e);
//		}
//		
//		log.info("user counts :"+list.size());
//		
//		return new ResponseEntity<List<SampleUser>> (list, HttpStatus.OK);
//	}
//	
//	@ApiOperation(value="사용자 정보 가져오기")
//	@GetMapping(value="/users/{userId}")
//	public ResponseEntity <SampleUser> getUsrId(@PathVariable String userId) { 
//		
//		SampleUser user = null;
//		try {
//			log.info("Start db select");
//			user = sampleUserDao.selectUser(userId);
//		} catch (Exception e) {
//			log.error("ERROR", e);
//			throw new RuntimeException(e);
//		}
//		
//		log.info("user info :{}", user);
//		
//		return new ResponseEntity<SampleUser> (user, HttpStatus.OK);
//	}
//	
//	@ApiOperation(value="사용자 등록")
//	@PostMapping(value="/users")
//	public ResponseEntity <String> insertUser(@RequestBody SampleUser user) { 
//		
//		int rc = 0;
//		String msg = null;
//		
//		try {
//			log.info("Start insert DB");
//			rc = sampleUserDao.insertUser(user);
//		} catch (Exception e) {
//			log.error("ERROR", e);
//			throw new RuntimeException(e);
//		}
//		log.info("add user rc:"+rc);
//		
//		if (rc > 0) {
//			msg =  "등록 성공";
//		}
//		
//		return new ResponseEntity<String> (msg, HttpStatus.OK);
//	}
//	
//	@ApiOperation(value="사용자 수정")
//	@PutMapping(value="/users/{userId}")
//	public ResponseEntity <String> updateUser(@PathVariable String userId, @RequestBody SampleUser user) { 
//		
//		int rc = 0;
//		String msg = null;
//		
//		try {
//			log.info("Start update DB");
//			user.setUserId(userId);
//			
//			rc = sampleUserDao.updateUser(user);
//		} catch (Exception e) {
//			log.error("ERROR", e);
//			throw new RuntimeException(e);
//		}
//		log.info("update user rc:"+rc);
//		
//		if (rc > 0) {
//			msg =  "수정 성공";
//		}
//		
//		return new ResponseEntity<String> (msg, HttpStatus.OK);
//	}	
//	
//	@ApiOperation(value="사용자 삭제")
//	@DeleteMapping(value="/users/{userId}")
//	public ResponseEntity <String>deleteUser(@PathVariable String userId) { 
//		
//		int rc = 0;
//		String msg = null;
//		
//		try {
//			log.info("Start update DB");
//			rc = sampleUserDao.deleteUser(userId);
//		} catch (Exception e) {
//			log.error("ERROR", e);
//			throw new RuntimeException(e);
//		}
//		log.info("delete user rc:{}",rc);
//		
//		if (rc > 0) {
//			msg =  "삭제 성공";
//		}
//		
//		return new ResponseEntity<String> (msg, HttpStatus.OK);
//	}	
}

