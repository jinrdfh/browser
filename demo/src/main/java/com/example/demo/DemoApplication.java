package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import java.util.*;
import java.net.*;
import java.io.*;

@SpringBootApplication
@RestController
public class DemoApplication {
	/* local data */
	public String urlBook[] = null;
	public Map<String, List< Integer > > findUrlIdByKey = new HashMap<String, List< Integer > >(); 
	public boolean readDataFlag = false;

	boolean readData(String fileName) {
		BufferedReader reader = null;
		String tpState = "start";
		int iBookLen = 0;
		readDataFlag = true;
		try {
			FileInputStream fileInputStream = new FileInputStream(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String[] words = tempString.split(",");
				if (tpState.equals("start"))
				{
					if (words[0].equals("Book"))
					{
						/* right file */
						iBookLen = Integer.valueOf(words[1]);
						urlBook = new String[iBookLen];
						/* next state */
						tpState = "Book";
					}
					else
					{
						System.out.println("ERROR wrong file: " + fileName + " word: " + words[0]);
						return false;
					}
				}
				else if (tpState.equals("Book"))
				{
					if (words[0].equals("Key"))
					{
						/* next state */
						tpState = "Key";
					}
					else
					{
						int urlId = Integer.valueOf(words[0]);
						urlBook[urlId] = words[1];
					}
				}
				else if (tpState.equals("Key"))
				{
					String tpKey = words[0];
					List< Integer > tpPosList = new ArrayList< Integer >();
			        for (int i = 1; i < words.length; i++) {
			            tpPosList.add(Integer.valueOf(words[i]));
			        }
			        findUrlIdByKey.put(tpKey, tpPosList);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public static void main(String[] args) {
		DemoApplication oneDemo = new DemoApplication();
		oneDemo.readData("D:\\eclipseWorkSpace\\testData.txt");
        for (int i = 0; i < oneDemo.urlBook.length; i++) {
            System.out.println(oneDemo.urlBook[i]);
        }
        System.out.println(oneDemo.findUrlIdByKey.entrySet());

		SpringApplication.run(DemoApplication.class, args);
	}

	// @GetMapping("**")
	@GetMapping("/load")
	@ResponseBody
	String load(HttpServletRequest request) {
		return String.format("You are browsing %s with %s!",
			request.getRequestURI(), request.getQueryString());
	}
	@GetMapping("search")
	@ResponseBody
	String search(
		@RequestParam(name = "query", required = false, defaultValue = "there") 
		String name) {
		if (!readDataFlag)
		{
			readData("D:\\eclipseWorkSpace\\testData.txt");
			System.out.println("init");
		}
		String tpContent = "<h1>My Search Engine</h1>";
		tpContent += "<form action=\"search\" method=\"GET\">";
		tpContent += "<p>Search: <input type=\"text\" name=\"query\" /> <input type=\"submit\"/></p></form>";
		List< Integer > tpUrlPos = findUrlIdByKey.get(name);
		if (null == tpUrlPos)
		{
			/* empty */
			tpContent += "<h1>no record</h1>";
			return tpContent;
		}
        for (int iPos : tpUrlPos) {
            tpContent += "<h1><a href = \"" + urlBook[iPos] + "\">" + urlBook[iPos] + "</a></h1>";
        }
        return tpContent;
		
	}
	@GetMapping("greeting")
	@ResponseBody
	String sayHello(
		@RequestParam(name = "name", required = false, defaultValue = "there") 
		String name) {

		return "<h1>Hello " + name + "!</h1>";
	}
}