package com.flt;

import com.equity.supplier.sdk.dida.api.DidaApiImpl;
import com.flt.service.ProductGroupService;
import com.yofish.equity.product.biz.dto.DidaHotelImageDto;
import com.yofish.equity.product.biz.repository.ProductGroupAttachmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ProductGroupAttachmentServiceTest {

    @Autowired
    private ProductGroupAttachmentRepository productGroupAttachmentRepository;

    @Autowired
    private ProductGroupService productGroupService;

    @Value("${image.array.size:5000}")
    private Integer size = 5000;

    @Autowired
//    private DidaApiImpl didaApi;

    @Test
    public void findProductGroupAttachmentList() {
    }

    @Test
    public void updateProductGroupAttachmentList() throws IOException {
        String path = "C:\\Users\\fenglingtong\\Downloads\\HotelImage (3).csv";
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        /*GetStaticInformationRQ getStaticInformationRQ = GetStaticInformationRQ.builder().IsGetUrlOnly(true).StaticType(StaticType.HotelImage.name()).build();
        log.info("道旅入参：{}", getStaticInformationRQ.toString());
        GetStaticInformationRS staticInformation = didaApi.getStaticInformation(getStaticInformationRQ);
        if (staticInformation == null) {
        }
        log.info("道旅返回：{}", staticInformation.toString());

        URL url = new URL(staticInformation.getUrl());*/
        InputStreamReader inputStreamReader = new InputStreamReader(getInputStream(fileInputStream));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 50 * 1024 * 1024);
        CSVFormat csvFormat = CSVFormat.DEFAULT.withSkipHeaderRecord(true).withDelimiter('|').withQuote(null);
        CSVParser csvParser = new CSVParser(bufferedReader, csvFormat);
        List<DidaHotelImageDto> didaHotelImageDtoList = new ArrayList<>(size * 2);
        long i =0;
        for (CSVRecord csvRecord : csvParser) {
            i++;
            DidaHotelImageDto didaHotelImageDto = new DidaHotelImageDto();
            if (csvRecord.size() < 4) {
                log.error(csvRecord.toString());
                didaHotelImageDto.setHotelId(0 < (csvRecord.size() - 1) ? csvRecord.get(0) : null);
                didaHotelImageDto.setImageCaption(1 < (csvRecord.size() - 1) ? csvRecord.get(1) : null);
                didaHotelImageDto.setImageUrl(2 < (csvRecord.size() - 1) ? csvRecord.get(2) : null);
                didaHotelImageDto.setImageOrder(3 < (csvRecord.size() - 1) ? csvRecord.get(3) : null);
            } else {
                didaHotelImageDto.setHotelId(csvRecord.get(0));
                didaHotelImageDto.setImageCaption(csvRecord.get(1));
                didaHotelImageDto.setImageUrl(csvRecord.get(2));
                didaHotelImageDto.setImageOrder(csvRecord.get(3));
            }
            didaHotelImageDtoList.add(didaHotelImageDto);
            if (didaHotelImageDtoList.size() > size) {
                productGroupService.importHotelImageList(didaHotelImageDtoList);
            }
        }
        log.info("总行数：{}", i);
        productGroupService.importHotelImageList(didaHotelImageDtoList);
        bufferedReader.close();
        inputStreamReader.close();
        fileInputStream.close();


    }

    public static InputStream getInputStream(InputStream in) throws IOException {

        PushbackInputStream pushbackInputStream = new PushbackInputStream(in);
        int ch = pushbackInputStream.read();
        if (ch != 0xEF) {
            pushbackInputStream.unread(ch);
        } else if ((ch = pushbackInputStream.read()) != 0xBB) {
            pushbackInputStream.unread(ch);
            pushbackInputStream.unread(0xef);
        } else if ((ch = pushbackInputStream.read()) != 0xBF) {
            throw new IOException("错误的UTF-8格式文件");
        } else {
        }
        return pushbackInputStream;
    }
}