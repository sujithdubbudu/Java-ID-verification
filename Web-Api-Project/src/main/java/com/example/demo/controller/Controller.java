package com.example.demo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.controller.Controller.MultipartInputStreamFileResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@RestController
@RequestMapping("/api")
public class Controller {
	@RequestMapping(value="/img", method= RequestMethod.POST)
	@ResponseBody
	public JSONObject scan(@RequestParam MultiValueMap<String, Object> paramMap, @RequestParam MultipartFile scan_image) throws JsonMappingException, JsonProcessingException, ParseException {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
		if (null == scan_image) {
			if ((null == paramMap.getFirst("scan_image_url")) && (null == paramMap.getFirst("scan_image_base64"))) {
				throw new RuntimeException("scan_image_url/scan_image_base64 is required.");
			}
		} else {
			try {
				body.add("scan_image", new MultipartInputStreamFileResource(scan_image.getInputStream(),
						scan_image.getOriginalFilename()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		body.addAll(paramMap);
		final String baseUrl = "https://accurascan.com/api/v4/ocr/webdemo";
		URI uri = null;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		System.out.println("uri " + uri);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.add("Api-Key", "1659710698BnPzUVMaG7ea8vtfDhAbdOq5Iug0NWyq6qL8asWb");
		System.out.println("headers " + headers);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(body,
				headers);
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("entity " + requestEntity);
		String result  = restTemplate.postForObject(uri,requestEntity,String.class);
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(result);
		System.out.println(json);
		return json;
	}

	class MultipartInputStreamFileResource extends InputStreamResource {

		private final String filename;

		MultipartInputStreamFileResource(InputStream inputStream, String filename) {
			super(inputStream);
			this.filename = filename;
		}

		@Override
		public String getFilename() {
			return this.filename;
		}

		@Override
		public long contentLength() throws IOException {
			return -1; // we do not want to generally read the whole stream into memory ...
		}
	}

}
