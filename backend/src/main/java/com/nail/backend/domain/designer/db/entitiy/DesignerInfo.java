package com.nail.backend.domain.designer.db.entitiy;

import com.nail.backend.domain.user.db.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "designer_info")
public class DesignerInfo implements Serializable {

    @Id
    @Column(name = "designer_seq")
    Long designerSeq;

    @MapsId
    @ApiModelProperty(value = "유저 정보")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "designer_seq", referencedColumnName="user_seq")
    private User user;

    // 디자이너 프로필 이미지
    @ApiModelProperty(value = "디자이너 프로필 이미지")
    private String designerProfileImgUrl;

    // 유저 사업자 등록증
    @ApiModelProperty(value = "유저 사업자등록증 url")
    String designerCertificationUrl;

    // 디자이너 샵 이름
    @ApiModelProperty(value = "유저 디자이너 샵 이름")
    String designerShopName;

    // 디자이너 샵 주소
    @ApiModelProperty(value = "유저 디자이너 샵 주소")
    String designerAddress;

    // 디자이너 소개 글
    @ApiModelProperty(value = "유저 디자이너 소개 글")
    String designerInfoDesc;

    // 디자이너 소개 이미지
    @ApiModelProperty(value = "유저 디자이너 소개 이미지")
    String designerInfoImgUrl;
    
    // 디자이너 샵 오픈 시간
    @ApiModelProperty(value = "디자이너 샵 오픈 시간")
    String designerShopOpen;
    
    // 디자이너 샴 마감 시간
    @ApiModelProperty(value = "디자이너 샵 마감 시간")
    String designerShopClose;

    // 디자이너 번호
    @ApiModelProperty(value = "디자이너 번호")
    String designerTel;

    // 인증신청 등록날짜
    @ApiModelProperty(value = "인증신청 등록날짜")
    @CreatedDate
    LocalDateTime designerRegedAt;
}
