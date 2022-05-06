import ImageList from "@mui/material/ImageList";
import ImageListItem from "@mui/material/ImageListItem";
import axios from "axios";
import { useEffect, useState } from "react";
import ModalTest from "../../Community/Modal";

interface CommunityImgProp {
  communityImgSeq: number;
  communityImgUrl: string;
}
interface CommunityContentProps {
  communityImg: CommunityImgProp[];
  communitySeq: number;
}
const CommunityContent = () => {
  const [contents, setContens] = useState<CommunityContentProps[]>([]);
  const [modalState, setModalState] = useState(false);
  const [curCommunitySeq, setCurCommunitySeq] = useState(0);
  // 소통게시글 데이터 가져오기
  const ACCESS_TOKEN = new URL(window.location.href).searchParams.get("token");
  useEffect(() => {
    const fetchData = async () => {
      await axios({
        method: "get",
        url: `http://localhost:8080/api/community/cnt`,
        headers: {
          Authorization: `Bearer ${ACCESS_TOKEN}`,
        },
      })
        .then((res) => {
          setContens(res.data);
        })
        .catch((err) => {
          console.log(err);
        });
    };
    fetchData();
    console.log("소통게시글 데이터 가져오기");
  }, []);
  // 최신 소통게시글 20개

  return (
    <div style={{ display: "flex", justifyContent: "center", padding: "20px" }}>
      <ImageList
        sx={{ width: 800, height: 630, overflowY: "hidden" }}
        cols={5}
        rowHeight={50}
      >
        {contents.map((item, idx) => (
          <ImageListItem key={idx}>
            <img
              src={item.communityImg[0].communityImgUrl}
              onClick={() => {
                setModalState((prev: any) => !prev);
                setCurCommunitySeq(item.communitySeq);
              }}
            />
          </ImageListItem>
        ))}
        {modalState && (
          <ModalTest
            itemData={contents}
            communitySeq={curCommunitySeq}
            state={modalState}
          />
        )}
      </ImageList>
    </div>
  );
};

export default CommunityContent;
