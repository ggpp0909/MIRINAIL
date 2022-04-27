import styled from "styled-components"
import {useEffect, useState} from 'react'
import Slider from "react-slick";
import DesignerCarousel from "./DesignerCarousel";
import { getHotDesigner, getRecentDesigner } from "../../store/api";

export interface hotDesignersProps {
  designer_seq : number;
  user_nickname : string;
  user_profile_img : string;
  designer_shop_name : string;
  follow_follower_length : number;
  designs : number; 
}
export interface Props {
  items : hotDesignersProps[];
}

const Wrapper = styled.div`
* {
  margin: 0px;
  padding: 0px;
  position: relative;
  list-style: none;
  text-decoration: none;
  box-sizing: border-box;
  
  }

  

`

const MainFrame = styled.div`
  width :1300px;
  height: 100%;
  margin : 0 auto;

  .carouselGroup {
    margin-top:100px;
    
  }
  .subtitle {
    font-size: 32px;
    font-weight : bold;
    margin-bottom : 12px;
  }
 

`


const PageContentThema = () => {
  const settings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 3,
    slidesToScroll: 3,
    // nextArrow: <SampleNextArrow />,
    // prevArrow: <SamplePrevArrow />,
  };
  const [hotDesigners,setHotDesigniers] = useState([
    {
      designer_seq : 1,
      user_nickname : 'Designer1',
      user_profile_img : 'https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202010/22/2182ecd3-b8cc-4993-98a7-8c2568f9fbbc.jpg',
      designer_shop_name : 'Nailshop1',
      follow_follower_length : 50,
      designs : 40, 
    },
    {
      designer_seq : 2,
      user_nickname : 'Designer1',
      user_profile_img : 'https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202010/22/2182ecd3-b8cc-4993-98a7-8c2568f9fbbc.jpg',
      designer_shop_name : 'Nailshop1',
      follow_follower_length : 50,
      designs : 40, 
    },
    {
      designer_seq : 3,
      user_nickname : 'Designer1',
      user_profile_img : 'https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202010/22/2182ecd3-b8cc-4993-98a7-8c2568f9fbbc.jpg',
      designer_shop_name : 'Nailshop1',
      follow_follower_length : 50,
      designs : 40, 
    },
    {
      designer_seq : 4,
      user_nickname : 'Designer1',
      user_profile_img : 'https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202010/22/2182ecd3-b8cc-4993-98a7-8c2568f9fbbc.jpg',
      designer_shop_name : 'Nailshop1',
      follow_follower_length : 50,
      designs : 40, 
    },
    {
      designer_seq : 5,
      user_nickname : 'Designer1',
      user_profile_img : 'https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202010/22/2182ecd3-b8cc-4993-98a7-8c2568f9fbbc.jpg',
      designer_shop_name : 'Nailshop1',
      follow_follower_length : 50,
      designs : 40, 
    },
    {
      designer_seq : 6,
      user_nickname : 'Designer1',
      user_profile_img : 'https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202010/22/2182ecd3-b8cc-4993-98a7-8c2568f9fbbc.jpg',
      designer_shop_name : 'Nailshop1',
      follow_follower_length : 50,
      designs : 40, 
    },
    {
      designer_seq : 7,
      user_nickname : 'Designer1',
      user_profile_img : 'https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202010/22/2182ecd3-b8cc-4993-98a7-8c2568f9fbbc.jpg',
      designer_shop_name : 'Nailshop1',
      follow_follower_length : 50,
      designs : 40, 
    },
    {
      designer_seq : 8,
      user_nickname : 'Designer1',
      user_profile_img : 'https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202010/22/2182ecd3-b8cc-4993-98a7-8c2568f9fbbc.jpg',
      designer_shop_name : 'Nailshop1',
      follow_follower_length : 50,
      designs : 40, 
    },
    {
      designer_seq : 9,
      user_nickname : 'Designer1',
      user_profile_img : 'https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202010/22/2182ecd3-b8cc-4993-98a7-8c2568f9fbbc.jpg',
      designer_shop_name : 'Nailshop1',
      follow_follower_length : 50,
      designs : 40, 
    },
    {
      designer_seq : 10,
      user_nickname : 'Designer1',
      user_profile_img : 'https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202010/22/2182ecd3-b8cc-4993-98a7-8c2568f9fbbc.jpg',
      designer_shop_name : 'Nailshop1',
      follow_follower_length : 50,
      designs : 40, 
    },
    {
      designer_seq : 11,
      user_nickname : 'Designer1',
      user_profile_img : 'https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202010/22/2182ecd3-b8cc-4993-98a7-8c2568f9fbbc.jpg',
      designer_shop_name : 'Nailshop1',
      follow_follower_length : 50,
      designs : 40, 
    },
  ])

  const [hotDesigner,setHotDesigner] = useState<any>([])
  const [latestDesigner,setLatestDesigner] = useState<any>()

  useEffect(() => {
    getHotDesigner().then((res):any => setHotDesigner(res))  
    getRecentDesigner().then((res):any => setLatestDesigner(res))
  },[])
  
  return (
    <>
      <Wrapper>
        
        <MainFrame>
          <div className="carouselGroup">
            <div>
              <div className="subtitle">Hot Designers</div>
              <DesignerCarousel items={hotDesigner} />
            </div>
            <div style={{marginTop:"48px"}}>
              <div className="subtitle">New Designers</div>
              <DesignerCarousel items={latestDesigner} />
            </div>
            {/* <div style={{marginTop:"48px"}}>
              <div className="subtitle">High Rate Designers</div>
              <DesignerCarousel items={hotDesigners} />
            </div> */}
          </div>
        </MainFrame>
        
      </Wrapper>
    </>
  )
}

export default PageContentThema