package com.barclay.barclay.rest;

import com.barclay.barclay.model.BooksBO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.awt.print.Book;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/")

public class BooksController {

    public static final Logger logger = LoggerFactory.getLogger(BooksController.class);

    public static final String IMAGE_FILE_URL="https://s3-ap-southeast-1.amazonaws.com/he-public-data/bookimage816b123.json";
    public static final String BOOK_FILE_URL="https://s3-ap-southeast-1.amazonaws.com/he-public-data/books8f8fe52.json";

    @Autowired(required = true)
    RestTemplate restTemplate;

    @GetMapping("/")
    public  String homeBarclay(){
        return "<h1> WelCome to Barclay's Hackthon</h1>";
    }
    @GetMapping("/home")
    @Cacheable("books")
    public List<BooksBO> home(){
        List<BooksBO> booksBOList=null;
       try {
           String booksDetails = restTemplate.exchange(BOOK_FILE_URL, HttpMethod.GET, null, String.class).getBody();
           booksBOList = Arrays.asList(objectMapper.readValue(booksDetails, BooksBO[].class));
          // logger.info("response {} ", booksBOList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return booksBOList;
    }

    ObjectMapper objectMapper = new ObjectMapper();

    /*@GetMapping({"CacheData", "/booksDetails"})
    @Cacheable(value = "books")
    public List<BooksBO> getBookDet(){
         return "";
    }*/
    @GetMapping("/booksDetails")
    public List<BooksBO> getBookDetails(){

        String booksDetails="";
        List<BooksBO> booksListBO = null;
        try{
           booksDetails = restTemplate.exchange(BOOK_FILE_URL, HttpMethod.GET, null, String.class).getBody();
            //logger.info("booksDetails {}= ",booksDetails);
            booksListBO = Arrays.asList(objectMapper.readValue(booksDetails, BooksBO[].class));
            //logger.info("booksListBO {}= ",booksListBO);

        }catch (Exception e){
            e.printStackTrace();
        }
        return booksListBO;
    }

    @GetMapping("/searchBook")
    public List<BooksBO> searchBook(@RequestParam String serachBookName){

        String booksDetails="";
        List<BooksBO> booksListBO = null;
        List<BooksBO> searchBookResult=null;
        try{
            logger.info("serachBookName = "+serachBookName);
            booksDetails = restTemplate.exchange(BOOK_FILE_URL, HttpMethod.GET, null, String.class).getBody();
            booksListBO = Arrays.asList(objectMapper.readValue(booksDetails, BooksBO[].class));
            String finalSearchBookName = serachBookName.toLowerCase();
            searchBookResult =  booksListBO.stream().filter(
                    booksBO -> (booksBO.getTitle().toLowerCase()).contains(finalSearchBookName)
                    ).collect(Collectors.toList());
           // logger.info("searchBookResult = ",searchBookResult);
        }catch (Exception e){
            e.printStackTrace();
        }
        return searchBookResult;
    }

    @GetMapping("/sortByAvgRate")
    public List<BooksBO>  sortByAverageRate(){
        List<BooksBO> booksBOList=null;
        String booksDetails="";
        List<BooksBO>  booksBOComparator=null;
        try {
            booksDetails = restTemplate.exchange(BOOK_FILE_URL, HttpMethod.GET, null, String.class).getBody();
            booksBOList = Arrays.asList(objectMapper.readValue(booksDetails, BooksBO[].class));
            booksBOComparator = booksBOList.stream().sorted(
                    (books1, books2) -> books1.getAverage_rating().compareTo(books2.getAverage_rating())
            ).collect(Collectors.toList());
            /*logger.info(" booksBOComparator {}", booksBOComparator);*/
        }catch (Exception e){
            e.printStackTrace();
        }
        return booksBOComparator;
    }


    @GetMapping("/user")
    public Principal user(Principal principal){
        return principal;
    }
}
