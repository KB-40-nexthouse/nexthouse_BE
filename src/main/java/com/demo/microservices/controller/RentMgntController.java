package com.demo.microservices.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.demo.microservices.dao.RentCntrMgntDAO;
import com.demo.microservices.model.RentCntr;
import com.demo.microservices.model.RentCntrRslt;
import com.demo.microservices.model.RentDepoSendInfo;
import com.demo.microservices.model.imgMgnt;

import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class RentMgntController {
	
	@Autowired
	private RentCntrMgntDAO rentCntrMgntDAO;

	@Value("${garage.product.server}")
	String productServer;
	
	@Value("${garage.product.port}")
	String productPort;
	
	@ApiOperation(value="전체 임대계약목록 정보 목록 조회")
	@CrossOrigin
	@GetMapping(value="/RentCntrList/{custNo}")
	public ResponseEntity <List<RentCntr>> getRentCntrListAll(@PathVariable String custNo) { 
		
		List<RentCntr> list = null;
		try {
			log.info("Start db select");
			list = rentCntrMgntDAO.selectRentCntrAll(custNo);
		} catch (Exception e) {
			log.error("ERROR", e);
			throw new RuntimeException(e);
		}
		
		log.info("user counts :"+list.size());
		
		return new ResponseEntity<List<RentCntr>> (list, HttpStatus.OK);
	}

	@ApiOperation(value="고객선호모델임대계약등록")
	@CrossOrigin
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
	@CrossOrigin
	@PostMapping(value="/RentCntrNextStep/rentCntrNo={rentCntrNo}&progress={progress}")
	public ResponseEntity <String> updateRentCntrStep(@PathVariable String rentCntrNo, @PathVariable int progress ) { 
		
		String msg = null;
		String rentStDt = null;
		String fixDt = null;
		String entDt = null;
		
		char cd = 'A';
		progress = progress + 1;
		
		if(progress == 3) { fixDt = LocalDate.now().toString(); }
		else if(progress == 4) { entDt = LocalDate.now().toString(); }
		else if(progress == 5) { cd = 'C'; rentStDt = LocalDate.now().toString(); }
		
		try {
			rentCntrMgntDAO.updateRentCntrStep(rentCntrNo, progress, cd, rentStDt, fixDt, entDt);
		} catch (Exception e) {
			log.error("ERROR", e);
			throw new RuntimeException(e);
		}
		msg = "임대계약다음단계처리";
		return new ResponseEntity<String> (msg, HttpStatus.OK);
	}
	
	@ApiOperation(value="임대계약결과조회")
	@CrossOrigin
	@GetMapping(value="/RentCntrRslt/{rentCntrNo}")
	public ResponseEntity <List<RentCntrRslt>> getRentCntrRslt(@PathVariable String rentCntrNo) { 
		
		List<RentCntrRslt> list = null;
		
		try {
			log.info("Start db select");
			list = rentCntrMgntDAO.selectRentCntrRslt(rentCntrNo);
			log.info("End db select");
		} catch (Exception e) {
			log.error("ERROR", e);
			throw new RuntimeException(e);
		}
		
		log.info("user counts :"+list.size());
		
		return new ResponseEntity<List<RentCntrRslt>> (list, HttpStatus.OK);
	}
	
	@ApiOperation(value="임대보증금송금정보조회")
	@CrossOrigin
	@GetMapping(value="/RentDepoSndInf/{custNo}")
	public ResponseEntity <List<RentDepoSendInfo>> getRentDepoSndInf(@PathVariable String custNo) { 
		
		List<RentDepoSendInfo> list = null;
		
		try {
			log.info("Start db select");
			list = rentCntrMgntDAO.selectRentDepoSendInfo(custNo);
			log.info("End db select");
		} catch (Exception e) {
			log.error("ERROR", e);
			throw new RuntimeException(e);
		}
		
		log.info("user counts :"+list.size());
		
		return new ResponseEntity<List<RentDepoSendInfo>> (list, HttpStatus.OK);
	}
	
//	@ApiOperation(value="전자계약서저장")
//	@CrossOrigin
//	@RequestMapping(value = "/save.do", method = RequestMethod.POST, produces = "application/text; charset=UTF-8")
//	public int save(@RequestParam String rentCntrNo, Map<String, Object> paramMap) throws Exception {
//		
//		Map<String,Object> map = new HashMap<String, Object>();
//		
//		String base64 = paramMap.get("base64").toString();
//		
//		byte[] bytes = base64.getBytes();
//		
//		map.put("bytes",bytes);
//		
//		rentCntrMgntDAO.saveImg(rentCntrNo, map);
//		
//		return 1;
//	}
//	@ApiOperation(value="전자계약서저장")
//	@CrossOrigin
//	@RequestMapping(value = "/save.do", method = RequestMethod.POST, produces = "application/text; charset=UTF-8")
//	public int save(@RequestParam String rentCntrNo, String base64) throws Exception {
//		
//		rentCntrMgntDAO.saveImg(rentCntrNo, base64);
//		
//		return 1;
//	}
	
	@ApiOperation(value="이미지등록")
	@CrossOrigin
	@PostMapping(value="/save.do")
	public ResponseEntity <String> save(@RequestBody imgMgnt img) { 
		
		int rc = 0;
		String msg = null;
		
		try {
			log.info("Start insert DB");
			rc = rentCntrMgntDAO.saveImg(img);
		} catch (Exception e) {
			log.error("ERROR", e);
			throw new RuntimeException(e);
		}
		log.info("add user rc:"+rc);
		
		if (rc > 0) {
			msg =  "등록 성공";
		}
		
		return new ResponseEntity<String> (msg, HttpStatus.OK);
	}

	@ApiOperation(value="이미지조회")
	@CrossOrigin
	@GetMapping(value="/ImgSel/{rentCntrNo}")
	public ResponseEntity <List<imgMgnt>> getCustImg(@PathVariable String rentCntrNo) { 
		
		List<imgMgnt> list = null;
		
		try {
			log.info("Start db select");
			list = rentCntrMgntDAO.selectImg(rentCntrNo);
			log.info("End db select");
		} catch (Exception e) {
			log.error("ERROR", e);
			throw new RuntimeException(e);
		}
		
		log.info("user counts :"+list.size());
		
		return new ResponseEntity<List<imgMgnt>> (list, HttpStatus.OK);
	}
}

