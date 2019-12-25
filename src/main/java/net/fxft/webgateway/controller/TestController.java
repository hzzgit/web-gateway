package net.fxft.webgateway.controller;


import com.alibaba.fastjson.JSON;
import io.netty.buffer.UnpooledByteBufAllocator;
import net.fxft.webgateway.route.GatewayRoutes;
import net.fxft.webgateway.vo.JsonMessage;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Goddy on 2018/11/9.
 */
@RestController
@RequestMapping({"/test", GatewayRoutes.Base_Prefix})
public class TestController {
    private String fileName = "测试.xlsx";

//    @RequestMapping("/addorigin")
//    public String addorigin() {
//        CorsConfig.addOrigin("null");
//        return "true";
//    }

//    @Autowired
//    private RouteLocatorImpl routeLocator;
//    @Autowired
//    private ApplicationEventPublisher publisherAware;


//    @RequestMapping("/updateRoutes")
//    private String updateRoute() {
//        routeLocator.updateRoutes();
//        publisherAware.publishEvent(new RefreshRoutesEvent(this));
//        return "成功";
//    }



    @RequestMapping("/logintest.action")
    public Mono<ServerResponse> login2(ServerWebExchange exchange) {

        Mono<MultiValueMap<String, String>> formData =exchange.getFormData();

        return formData.flatMap(map -> {
            System.out.println(map.getFirst("username"));
            System.out.println(map.getFirst("password"));
            return ServerResponse.ok().contentType(MediaType.APPLICATION_STREAM_JSON)
                    .body(BodyInserters.fromObject(map));
        });

    }


    @RequestMapping("/logintest2.action")
    public Mono<JsonMessage> login3(ServerWebExchange exchange) {

        Mono<MultiValueMap<String, String>> formData =exchange.getFormData();

        return formData.map(map -> {
            System.out.println(map.getFirst("username"));
            System.out.println(map.getFirst("password"));
            return new JsonMessage();
        });

    }



    @GetMapping("selectMy")
    public Mono<Void> selectMy(ServerHttpRequest request, ServerHttpResponse response) {
        String pageNumber= request.getQueryParams().getFirst("pageNumber");
//        AuthUser authUser= LoginProcess.getCurrentUser(request.getCookies());
        Map<String,String> result=new HashMap<String,String>(){{
            put("aa","1");
            put("bb","21dd");
        }};


        return response.writeWith(Flux.create(sink -> {

            NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
            try {
                DataBuffer dataBuffer= nettyDataBufferFactory.wrap(JSON.toJSONString(result).getBytes("utf8"));
                //DataBuffer dataBuffer= nettyDataBufferFactory.wrap("asdf".getBytes("utf8"));
                sink.next(dataBuffer);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sink.complete();
        }));
    }

//    public Mono<ServerResponse> testresp() {
//        DataBufferUtils.readInputStream()
//        return ServerResponse.ok().body(BodyInserters.fromDataBuffers(
//
//                Mono<DataBuffer>
//
//                ));
//        return ServerResponse.ok().body(BodyInserters.fromDataBuffers(
//
//                Mono<DataBuffer>
//
//                DataBufferUtils.join(Flux.from(body))
//
//                new Publisher<DataBuffer>() {
//            @Override
//            public void subscribe(Subscriber<? super DataBuffer> s) {
//
//            }
//        }));
//    }



//    @GetMapping("/download/resource")
//    public Mono<Void> test1(ServerHttpResponse response) {
//        ClassPathResource resource = new ClassPathResource(fileName);
//        try {
//            File file = resource.getFile();
//            return downloadFile(response, file, fileName);
//        } catch (IOException e) {
//            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "未找到相应文件！"));
//        }
//    }
//
//    @GetMapping("/download/excel")
//    public Mono<Void> test2(ServerHttpResponse response) {
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("sheet-1");
//        sheet.createRow(0).createCell(0).setCellValue("你好啊");
//        sheet.createRow(2).createCell(2).setCellValue("nico");
//        return Mono.fromCallable(() -> {
//            File file = new File(fileName);
//            workbook.write(new FileOutputStream(file));
//            return file;
//        }).flatMap(file -> downloadFile(response, file, fileName));
//    }
//
//    @PostMapping("/upload")
//    public Mono<String> test3(@RequestPart FilePart filePart) {
//
//        DataBufferUtils.write(filePart.content(), channel, 0)
//                .doOnComplete(() -> {
//                    System.out.println("finish");
//                })
//                .subscribe();
//
//
//        return filePart2file(filePart).flatMap(file -> {
//            if (StringUtils.endsWithIgnoreCase(file.getName(), "xls") || StringUtils.endsWithIgnoreCase(file.getName(), "xlsx")) {
//                return Mono.fromCallable(() -> getWorkBook(file)).onErrorMap(e -> new ResponseStatusException(HttpStatus.NOT_FOUND, "上传失败！")).map(workbook -> workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
//            } else {
//                return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "上传失败！"));
//            }
//        });
//    }
//
//    private Mono<Void> downloadFile(ServerHttpResponse response, File file, String fileName) {
//        ZeroCopyHttpOutputMessage zeroCopyHttpOutputMessage = (ZeroCopyHttpOutputMessage) response;
//        try {
//            response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(URLEncoder.encode(fileName, StandardCharsets.UTF_8.displayName())));
//            return zeroCopyHttpOutputMessage.writeWith(file, 0, file.length());
//        } catch (UnsupportedEncodingException e) {
//            throw new UnsupportedOperationException();
//        }
//    }
//
//    private Workbook getWorkBook(File file) throws IOException {
//        FileInputStream fileInputStream = new FileInputStream(file);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        IOUtils.copy(fileInputStream, byteArrayOutputStream);
//        Workbook workbook = null;
//        try {
//            POIFSFileSystem fs = new POIFSFileSystem(file);
//            workbook = new HSSFWorkbook(fs);
//        } catch (Exception e) {
//            workbook = new XSSFWorkbook(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
//        }
//        return workbook;
//    }
//
//    private Mono<File> filePart2file(FilePart filePart) {
//        return Mono.fromCallable(() -> {
//            Path path = Files.createTempFile(null, filePart.filename());
//            File file = path.toFile();
//            filePart.transferTo(file);
//            return file;
//        }).onErrorMap(error -> new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "读取文件失败！"));
//    }
}