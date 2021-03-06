package com.nail.backend.domain.nailart.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nail.backend.domain.book.db.repository.BookRepository;
import com.nail.backend.domain.designer.db.entitiy.DesignerInfo;
import com.nail.backend.domain.designer.db.repository.DesignerInfoRepository;
import com.nail.backend.domain.favorite.db.entity.Favorite;
import com.nail.backend.domain.nailart.db.repository.NailartRepositorySupport;
import com.nail.backend.domain.nailart.request.NailartUpdatePutReq;
import com.nail.backend.domain.nailart.response.DesignerNailartListRes;
import com.nail.backend.domain.nailart.response.NailartListGetRes;
import com.nail.backend.domain.designer.db.repository.DesignerRepository;
import com.nail.backend.domain.nailart.db.entity.Nailart;
import com.nail.backend.domain.nailart.db.entity.NailartImg;
import com.nail.backend.domain.nailart.db.repository.NailartImgRepository;
import com.nail.backend.domain.nailart.db.repository.NailartRepository;
import com.nail.backend.domain.nailart.request.NailartRegisterPostReq;
import com.nail.backend.domain.nailart.response.NailartDetailGetRes;
import com.nail.backend.domain.review.db.entity.Review;
import com.nail.backend.domain.review.db.entity.ReviewComment;
import com.nail.backend.domain.review.db.entity.ReviewImg;
import com.nail.backend.domain.review.response.ReviewCommentGetRes;
import com.nail.backend.domain.review.response.ReviewGetRes;
import com.nail.backend.domain.user.db.entity.User;
import com.nail.backend.domain.user.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor

@Service
@Component
public class NailartServiceImpl implements NailartService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    @Autowired
    NailartRepository nailartRepository;

    @Autowired
    NailartRepositorySupport nailartRepositorySupport;

    @Autowired
    NailartImgRepository nailartImgRepository;

    @Autowired
    DesignerRepository designerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DesignerInfoRepository designerInfoRepository;

    @Autowired
    BookRepository bookRepository;

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "????????? ????????? ??????(" + fileName + ") ?????????.");
        }
    }

    @Override
    public List<NailartListGetRes> nailartList(String category, String color, String type, String sort, int page, int size) {
        List<NailartListGetRes> nailart = new ArrayList<>();

        if(category.equals("color")){// color category
            if(color != ""){// ????????? ????????? ?????????
                if(sort.equals("like")){// ????????? ???
                    nailart = nailartRepositorySupport.getListbyColorFavoite(color, page, size);
                }else{ // ?????????
                    System.out.println("dddd");
                    nailart = nailartRepositorySupport.getListbyColorLatest(color, page, size);
                }
            }else{// ????????? ????????? ?????????
                if(sort.equals("like")){// ????????? ???
                    nailart = nailartRepositorySupport.getListbyFavoite( page, size);
                }else{ // ?????????
                    System.out.println("check!!");
                    nailart = nailartRepositorySupport.getListbyLatest(page, size);
                }
            }
        } else if(category.equals("type")){// type category
            System.out.println("check1");
            if(type != ""){// ????????? ?????? ???
                System.out.println("check2");
                if(sort.equals("like")){// ????????? ???
                    nailart = nailartRepositorySupport.getListbyTypeFavoite(type, page, size);
                }else{ // ?????????
                    System.out.println(type);
                    nailart = nailartRepositorySupport.getListbyTypeLatest(type, page, size);
                }

            }else{// ????????? ?????????
                System.out.println("check3");
                if(sort.equals("like")){// ????????? ???
                    nailart = nailartRepositorySupport.getListbyFavoite( page, size);
                }else{ // ?????????
                    nailart = nailartRepositorySupport.getListbyLatest(page, size);
                }
            }
        }else{// ???????????? ?????? ????????????
            System.out.println("check4");
            nailart = nailartRepositorySupport.getListbyLatest(page, size);
        }

        return nailart;
    }

    @Override
    public List<NailartListGetRes> otherNailart(long designerSeq, long nailartSeq) {
        return nailartRepositorySupport.getOtherNailartByDesignerSeq(designerSeq, nailartSeq);
    }

    @Override
    public DesignerNailartListRes getdesignerNailartList(long designerSeq, int page, int size) {
        return nailartRepositorySupport.getdesignerNailartList(designerSeq, page, size);
    }

    @Override
    public NailartDetailGetRes nailartDetail(long nailartSeq) {
        // ?????? ?????? ????????? ?????? ????????? ?????? ??????????????? ???????????? ?????? ????????? ???????????? ???????
        // ????????? ?????? ?????? ????????? ???????????? controller?????? ??????????\
        NailartDetailGetRes nailartDetailGetRes = new NailartDetailGetRes();
        Nailart nailart = nailartRepository.findByNailartSeq(nailartSeq);
        DesignerInfo designerInfo = designerInfoRepository.findByDesignerSeq(nailart.getDesignerSeq());
        nailartDetailGetRes.setNailartSeq(nailart.getNailartSeq());
        nailartDetailGetRes.setDesignerImgUrl(designerInfo.getDesignerProfileImgUrl());
        nailartDetailGetRes
                .setDesignerNickname(userRepository.findByUserSeq(nailart.getDesignerSeq()).getUserNickname());
        nailartDetailGetRes.setDesignerSeq(nailart.getDesignerSeq());
        nailartDetailGetRes.setNailartName(nailart.getNailartName());
        nailartDetailGetRes.setNailartDesc(nailart.getNailartDesc());
        nailartDetailGetRes.setNailartType(nailart.getNailartType());
        nailartDetailGetRes.setNailartColor(nailart.getNailartColor());
        nailartDetailGetRes.setNailartDetailColor(nailart.getNailartDetailColor());
        nailartDetailGetRes.setNailartWeather(nailart.getNailartWeather());
        nailartDetailGetRes.setNailartAvailable(nailart.isNailartAvailable());
        nailartDetailGetRes.setNailartThumbnailUrl(nailart.getNailartThumbnailUrl());
        nailartDetailGetRes.setDesignerShopName(designerInfo.getDesignerShopName());
        nailartDetailGetRes.setNailartPrice(nailart.getNailartPrice());
        nailartDetailGetRes.setNailartRegedAt(nailart.getNailartRegedAt());
        nailartDetailGetRes.setNailartRating(nailart.getNailartRating());
        nailartDetailGetRes.setNailartImgUrl(nailartImgRepository.findByNailartSeq(nailartSeq).getNailartImgUrl());
        nailartDetailGetRes.setNailartNft(nailart.getNailartNft());

        return nailartDetailGetRes;
    }

    @Override
    public Nailart nailartRegister(NailartRegisterPostReq nailartRegisterPostReq, List<MultipartFile> files) {
        Nailart nailart = new Nailart();
        NailartImg nailartImg = new NailartImg();
        Nailart nailartSaved = new Nailart();
        // ?????? ????????? ?????? ????????? ???????????? ??????.
        // ????????? ?????? ????????? ???????????? ??????.

        System.out.println("???????????? ????????????.");
        System.out.println(nailartRegisterPostReq);
        System.out.println(files);
        int index = 0;
        for (MultipartFile file : files) {
            System.out.println("q???????????????");
            if (index == 0) {
                System.out.println("????????????????");
                // System.out.println(files);
                nailart.setDesignerSeq(nailartRegisterPostReq.getDesignerSeq());
                nailart.setNailartName(nailartRegisterPostReq.getNailartName());
                nailart.setNailartDesc(nailartRegisterPostReq.getNailartDesc());
                nailart.setNailartType(nailartRegisterPostReq.getNailartType());
                nailart.setNailartColor(nailartRegisterPostReq.getNailartColor());
                nailart.setNailartDetailColor(nailartRegisterPostReq.getNailartDetailColor());
                nailart.setNailartWeather(nailartRegisterPostReq.getNailartWeather());
                nailart.setNailartPrice(nailartRegisterPostReq.getNailartPrice());
                nailart.setNailartRegedAt(Timestamp.valueOf(LocalDateTime.now()));
                // ????????? ?????????
                String fileName = createFileName(file.getOriginalFilename());
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(file.getSize());
                objectMetadata.setContentType(file.getContentType());
                System.out.println(nailart);
                System.out.println(fileName);
                try (InputStream inputStream = file.getInputStream()) {
                    System.out.println("s3 ??????");
                    System.out.println("bucket :  " + bucket);
                    System.out.println("fileName : " + fileName);
                    System.out.println("inputStream : " + file.getInputStream());
                    System.out.println("objectMetadata : " + objectMetadata);
                    amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                    System.out.println("s3?????????????");
                } catch (IOException e) {
                    System.out.println("s3 ?????? ??????");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "????????? ???????????? ??????????????????.");
                }
                //
                System.out.println("???????????????????");
                nailart.setNailartThumbnailUrl(amazonS3.getUrl(bucket, fileName).toString());
                System.out.println(nailart);
                nailartSaved = nailartRepository.save(nailart);
                nailart.setNailartSeq(nailartSaved.getNailartSeq());
            } else {
                // ????????? ?????????
                String fileName = createFileName(file.getOriginalFilename());
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(file.getSize());
                objectMetadata.setContentType(file.getContentType());
                System.out.println("???????????????");
                try (InputStream inputStream = file.getInputStream()) {
                    System.out.println("??????????????? s3 ??????");
                    amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                } catch (IOException e) {
                    System.out.println("??????????????? s3 ?????? ??????");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "????????? ???????????? ??????????????????.");
                }
                nailartImg.setNailartSeq(nailartSaved.getNailartSeq());
                nailartImg.setNailartImgUrl(amazonS3.getUrl(bucket, fileName).toString());

                System.out.println(nailartImg);
                nailartImgRepository.save(nailartImg);
            }
            index++;
        }
        System.out.println("??????????????????????");
        System.out.println(nailart);
        return nailart;
    }

    @Override
    @Transactional
    public Nailart nailartUpdate(NailartUpdatePutReq nailartUpdatePutReq, List<MultipartFile> files) {
        Nailart nailart = new Nailart();
        NailartImg nailartImg = new NailartImg();
        Nailart nailartSaved = new Nailart();

        int index = 0;
        for (MultipartFile file : files) {
            if (index == 0) {
                System.out.println("check1!");
                // ????????? ?????????
                String fileName = createFileName(file.getOriginalFilename());
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(file.getSize());
                objectMetadata.setContentType(file.getContentType());
                try (InputStream inputStream = file.getInputStream()) {
                    amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "????????? ???????????? ??????????????????.");
                }
                System.out.println("check2!");
                nailartUpdatePutReq.setNailartThumbnailUrl(amazonS3.getUrl(bucket, fileName).toString());
                System.out.println("check3!");
                System.out.println(nailartUpdatePutReq);
                nailartRepositorySupport.updateNailartByNailartSeq(nailartUpdatePutReq);
                System.out.println("check4!");
            } else {
                // ????????? ?????????
                String fileName = createFileName(file.getOriginalFilename());
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(file.getSize());
                objectMetadata.setContentType(file.getContentType());
                System.out.println("???????????????");
                try (InputStream inputStream = file.getInputStream()) {
                    System.out.println("??????????????? s3 ??????");
                    amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                } catch (IOException e) {
                    System.out.println("??????????????? s3 ?????? ??????");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "????????? ???????????? ??????????????????.");
                }
                System.out.println("check5!");
                System.out.println(nailartUpdatePutReq.getNailartSeq());
                NailartImg Img = nailartImgRepository.findByNailartSeq(nailartUpdatePutReq.getNailartSeq());
                System.out.println(Img.getNailartImgSeq());
                nailartImgRepository.deleteById(Img.getNailartImgSeq());
                System.out.println("check6!");
                nailartImg.setNailartSeq(nailartUpdatePutReq.getNailartSeq());
                System.out.println("check7!");
                nailartImg.setNailartImgUrl(amazonS3.getUrl(bucket, fileName).toString());
                System.out.println("check8!");
                nailartImgRepository.save(nailartImg);
                System.out.println("check9!");
            }
            index++;
        }
        return nailart;
    }

    @Override
    @Transactional
    public boolean nailartAvailableUpdate(long nailartSeq) {
        return nailartRepositorySupport.updateNailartAvailableByNailartSeq(nailartSeq);
    }

    @Override
    public boolean nailartNftUpdate(long nailartSeq, String nailartNft) {
        return nailartRepositorySupport.updateNailartNft(nailartSeq, nailartNft);
    }


    // sac ------------------------------------------------------------------------------
    // ???????????? ??????
    @Override
    @Transactional
    public Page<NailartListGetRes> getNailartListByNailartName(Pageable pageable, String name){

        Page<Nailart> nailartList = nailartRepository.searchByNailartName(name,pageable);
        List<NailartListGetRes> nailartGetResList = new ArrayList<>();

        long total = nailartList.getTotalElements();

        for (Nailart n : nailartList) {
            DesignerInfo designer = designerRepository.findById(n.getDesignerSeq()).orElse(null);

            // nailart ?????? ?????? ????????? ?????????
            NailartListGetRes nailartGetRes = NailartListGetRes.builder()
                    .nailartSeq(n.getNailartSeq())
                    .nailartName(n.getNailartName())
                    .nailartDesc(n.getNailartDesc())
                    .nailartType(n.getNailartType())
                    .nailartColor(n.getNailartColor())
                    .nailartDetailColor(n.getNailartDetailColor())
                    .nailartWeather(n.getNailartWeather())
                    .nailartThumbnailUrl(n.getNailartThumbnailUrl())
                    .nailartPrice(n.getNailartPrice())
                    .nailartAvailable(n.isNailartAvailable())
                    .nailartRegedAt(n.getNailartRegedAt())
                    .nailartRating(n.getNailartRating())
                    .build();
            nailartGetResList.add(nailartGetRes);
        }
        Page<NailartListGetRes> res = new PageImpl<>(nailartGetResList, pageable, total);

        return res;


    }
}
