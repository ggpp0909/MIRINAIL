import styled from "styled-components";
import { useState, useEffect } from "react";
import Box from "@mui/material/Box";
import Modal from "@mui/material/Modal";
import axios from "axios";
import TimeCounting from "time-counting";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { useNavigate } from "react-router-dom";
const StyledSlider = styled(Slider)`
  .slick-next {
    right: 1%;
  }
  .slick-prev {
    left: 1%;
    z-index: 9;
  }
  .slick-dots {
    bottom: 10px;
  }
  height: 100%;
  .slick-list {
    height: 100%;
  }
  .slick-track {
    height: 100%;
    div {
      height: 100%;
    }
  }
  img {
    height: 100%;
  }
`;

const modalStyle = {
  position: "absolute" as "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: "70%",
  height: "90%",
  bgcolor: "background.paper",
  border: "2px solid #000",
  boxShadow: 24,
  zIndex: '9999',
  // p: 4,
};

const Wrapper = styled.div`
  display: flex;
  height: 100%;
  .leftDetailBox {
    width: 60%;
    height: 100%;
  }

  .rightDetailBox {
    padding-bottom: 30px;
    position: relative;
    // display :flex;
    // flex-directioin : column;
    background-color: #fff;
    width: 40%;

    img {
      border-radius: 70px;
      -moz-border-radius: 70px;
      -khtml-border-radius: 70px;
      -webkit-border-radius: 70px;
    }

    .rightDetailBoxTop {
      height: 72px;
      // line-height :40px;
      display: flex;
      padding: 14px 4px 14px 16px;
      -webkit-box-align: center;
      -webkit-align-items: center;
      -ms-flex-align: center;
      align-items: center;
      -webkit-box-orient: horizontal;
      -webkit-box-direction: normal;
      border-bottom: 1px solid #e3e3e3;
    }
    .rightDetailBoxMiddle {
      // margin-top : 15px;
      padding: 16px;
      overflow-y: scroll;
      height: 500px;
      flex: 1;
      .postWriter {
        display: flex;
        margin-bottom: 16px;
      }
      .replys {
        .replyFrame {
          margin-bottom: 16px;
        }
      }
      .replysInfo {
        display: flex;
      }
    }

    .inputBox {
      // position: relative;
      // bottom : 0px;
      height: 48px;
      display: flex;
      background-color: #fff;
      input {
        width: 90%;
        border: 0px;
      }
      input:focus {
        outline: none;
      }
      div {
        line-height: 50px;
        width: 10%;
        text-align: center;
        color: #b3dffc;
      }
      hr {
        color: #e3e3e3;
      }
    }
  }
`;

export default function ModalTest({ contents, communitySeq, state }: any) {
  const settings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
  };
  interface CommunityImgProp {
    communityImgSeq: number;
    communityImgUrl: string;
  }
  interface CommunityItemProp {
    communityImg: CommunityImgProp[];
    communityTitle: string;
    communitySeq: number;
    rows: number;
    cols: number;
  }
  interface CommunityDetailProp {
    communitySeq: number;
    userNickname: string;
    userProfileImg: string;
    userSeq: string;
    communityTitle: string;
    communityDesc: string;
    communityImg: CommunityImgProp[];
    communityRededAt: string;
    communityCommentLayerCnt: number;
    communityCommentLayerExist: boolean;
  }
  interface CommentDataProp {
    communityCommentSeq: number;
    userSeq: number;
    userNickname: string;
    userProfileImg: string;
    communityCommentDesc: string;
    communityCommentRegedAt: string;
    communityCommentIsDelete: boolean;
  }
  interface CommentPostDataProp {
    communityCommentDesc: string;
    communityCommentLayer: number;
    communitySeq: number;
    communityCommentSeq: number | null;
  }

  const [itemData, setItemData] = useState<CommunityItemProp[]>(contents);
  const [itemDetail, setItemDetail] = useState<CommunityDetailProp>();
  const [commentData, setCommentData] = useState<CommentDataProp[]>([]);
  const [replyData, setreplyData] = useState<CommentDataProp[]>([]);
  const [communityCommentLayer, setCommunityCommentLayer] = useState<number>(1);
  const [currentCommunitySeq, setCurrentCommunitySeq] = useState<number>(0);
  const [currentCommentSeq, setCurrentCommentSeq] = useState<number>(0);
  const [commentPostData, setCommentPostData] = useState<CommentPostDataProp>();
  const [currentCommunityIdx, setCurrentCommunityIdx] = useState<number>(0);
  const [inputVal, setInputVal] = useState<string>("");
  const [time, setTime] = useState<string>("");
  const [communityTime, setCommunityTime] = useState<string>("");
  const [tagName, setTagName] = useState("");
  const navigate = useNavigate();

  const ACCESS_TOKEN = localStorage.getItem("token");
  useEffect(() => {
    getDetail(communitySeq);
  }, []);
  //?????? ?????? ????????????
  const getNowTime = () => {
    var today = new Date();
    var year = today.getFullYear();
    var month = ("0" + (today.getMonth() + 1)).slice(-2);
    var day = ("0" + today.getDate()).slice(-2);
    var hours = ("0" + today.getHours()).slice(-2);
    var minutes = ("0" + today.getMinutes()).slice(-2);
    var seconds = ("0" + today.getSeconds()).slice(-2);

    var dateString = year + "-" + month + "-" + day;
    var timeString = hours + ":" + minutes + ":" + seconds;
    setTime(dateString + " " + timeString);
  };
  //????????? ????????????
  const option = {
    lang: undefined,
    objectTime: time,
    calculate: {
      justNow: 60,
    },
  };

  //???????????? ????????? ????????????
  const deleteCommunity = async (communitySeq: number) => {
    await axios({
      method: "delete",
      url: `https://k6e101.p.ssafy.io/api/community/${communitySeq}`,
      headers: {
        Authorization: `Bearer ${ACCESS_TOKEN}`,
      },
    })
      .then((res) => {
        setModalStatus((prev: any) => !prev);
        var array = [...itemData];
        array.splice(currentCommunityIdx, 1);
        setItemData(array);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  //?????? ????????????
  const deleteComment = async (communityCommentSeq: number) => {
    axios({
      method: "patch",
      url: `https://k6e101.p.ssafy.io/api/community/comment/${communityCommentSeq}`,
      headers: {
        Authorization: `Bearer ${ACCESS_TOKEN}`,
      },
    })
      .then((res) => {
        {
          communityCommentLayer === 3
            ? getReplyComments(communityCommentSeq)
            : getComments(currentCommunitySeq);
        }
        setOpen("");
      })
      .catch((err) => {
        console.log(err);
      });
  };

  //?????? ????????????
  const getComments = async (communitySeq: number) => {
    if (ACCESS_TOKEN) {
      axios({
        method: "get",
        url: `https://k6e101.p.ssafy.io/api/community/comment/${communitySeq}`,
        params: {
          page: 0,
          size: 10,
        },
        headers: {
          Authorization: `Bearer ${ACCESS_TOKEN}`,
        },
      })
        .then((res) => {
          console.log("?????? ?????????", res);
          setCommentData(res.data.content);
        })
        .catch((err) => {
          console.log(err);
        });
    }
  };

  //????????? ????????????
  const getReplyComments = async (communityCommentSeq: number) => {
    if (ACCESS_TOKEN) {
      axios({
        method: "get",
        url: `https://k6e101.p.ssafy.io/api/community/comment/layer/${communityCommentSeq}`,
        params: {
          page: 0,
          size: 10,
        },
        headers: {
          Authorization: `Bearer ${ACCESS_TOKEN}`,
        },
      })
        .then((res) => {
          console.log("????????? ?????????", res);
          setreplyData(res.data.content);
        })
        .catch((err) => {
          console.log(err);
        });
    }
  };

  //????????? ?????? ?????? ????????????
  const getDetail = async (communitySeq: number) => {
    if (ACCESS_TOKEN) {
      axios({
        method: "get",
        url: `https://k6e101.p.ssafy.io/api/community/${communitySeq}`,
        headers: {
          Authorization: `Bearer ${ACCESS_TOKEN}`,
        },
      })
        .then((res) => {
          setItemDetail(res.data);
          //   setModalStatus((prev: any) => !prev);
          getComments(communitySeq);
          setCurrentCommunitySeq(communitySeq);
          console.log(res.data.communityRegedAt, "????????? ?????? ?????????");
          let temp = TimeCounting(res.data.communityRegedAt, option);
          setCommunityTime(temp);
          sessionStorage.setItem("communitySeq", communitySeq.toString());
          getNowTime();
        })
        .catch((err) => {
          console.log(err);
        });
    }
  };

  // ?????? ??????

  const tagUser = (userName: string, communityCommentSeq: number) => {
    setTagName("@" + userName + " ");
    // setInputVal("@" + userName + " ");
    setCommunityCommentLayer(3);
    setCurrentCommentSeq(communityCommentSeq);
  };
  //?????? ??????
  const onChangeText = (content: string) => {
    setInputVal(content);
    if (communityCommentLayer === 3) {
      setCommentPostData({
        communityCommentDesc: content,
        communityCommentLayer: communityCommentLayer,
        communitySeq: currentCommunitySeq,
        communityCommentSeq: currentCommentSeq,
      });
    } else {
      setCommentPostData({
        communityCommentDesc: content,
        communityCommentLayer: communityCommentLayer,
        communitySeq: currentCommunitySeq,
        communityCommentSeq: currentCommentSeq,
      });
    }

    console.log(commentPostData, "???????????????");
  };

  //?????? ????????????
  const createComment = async () => {
    if (commentPostData !== undefined) {
      await axios({
        method: "post",
        url: `https://k6e101.p.ssafy.io/api/community/comment`,
        data: commentPostData,
        headers: {
          "Content-type": "application/json",
          Authorization: `Bearer ${ACCESS_TOKEN}`,
        },
      })
        .then((res) => {
          getComments(currentCommunitySeq);
          setInputVal("");
          setOpen("");
          setTagName("");
        })
        .catch((err) => {
          console.log(err);
        });
    }
  };

  const Button = () => {
    if (inputVal) {
      return (
        <div
          style={{ color: "#0095f6", width: "100px", marginLeft: "5px" }}
          onClick={createComment}
        >
          ??????
        </div>
      );
    } else {
      return <div style={{ width: "100px", marginLeft: "5px" }}>??????</div>;
    }
  };

  // collapse
  const [open, setOpen] = useState("");
  const toggle = (className: any) => {
    if (open === className) {
      setOpen("");
    } else {
      setOpen(className);
    }
  };

  // ??????
  const [modalStatus, setModalStatus] = useState(state);
  const handleOpen = () => setModalStatus(true);
  const handleClose = () => {
    setModalStatus(false);
    setOpen("");
    setTagName("");
  };

  useEffect(() => {}, [modalStatus]);

  return (
    <Modal
      open={modalStatus}
      onClose={handleClose}
      aria-labelledby="modal-modal-title"
      aria-describedby="modal-modal-description"
    >
      <Box sx={modalStyle}>
        <Wrapper>
          <div className="leftDetailBox" style={{ overflow: "hidden" }}>
            <StyledSlider {...settings}>
              {itemDetail?.communityImg.map((item, idx) => {
                return <img src={item.communityImgUrl} alt="" />;
              })}
            </StyledSlider>
          </div>
          <div
            className="rightDetailBox"
            style={{ display: "flex", flexDirection: "column" }}
          >
            <div
              className="rightDetailBoxTop"
              style={{ justifyContent: "space-between" }}
            >
              <div>
                <img
                  src={itemDetail?.userProfileImg}
                  alt=""
                  width="32"
                  height="32"
                  style={{ marginRight: "14px" }}
                />
                {itemDetail?.userNickname}
              </div>
              {itemDetail?.userNickname ===
                sessionStorage.getItem("userNickname") && (
                <div>
                  <button
                    onClick={() => {
                      navigate("/community/update", {
                        state: {
                          desc: itemDetail?.communityDesc,
                          title: itemDetail.communityTitle,
                          img: itemDetail?.communityImg,
                        },
                      });
                    }}
                  >
                    ??????
                  </button>

                  <button
                    style={{ color: "red" }}
                    onClick={() => {
                      deleteCommunity(itemDetail.communitySeq);
                    }}
                  >
                    ??????
                  </button>
                </div>
              )}
            </div>
            <div className="rightDetailBoxMiddle">
              <div className="postWriter" style={{ display: "flex" }}>
                <img
                  src={itemDetail?.userProfileImg}
                  alt=""
                  width="32"
                  height="32"
                />
                <div style={{ marginLeft: "14px", whiteSpace: "nowrap" }}>
                  {itemDetail?.userNickname}
                </div>
                <div style={{ marginLeft: "14px" }}>
                  <div>{itemDetail?.communityDesc}</div>
                  {itemDetail ? (
                    <div style={{ marginTop: "10px" }}>{communityTime}</div>
                  ) : (
                    <></>
                  )}
                </div>
              </div>
              <div className="replys">
                {commentData.map((e: any, idx) => {
                  return (
                    <div className="replyFrame" key={idx}>
                      {e.communityCommentIsDelete ? (
                        <div className="replysInfo">
                          <div style={{ borderRadius: "70%" }}>
                            <img
                              src={e.userProfileImg}
                              alt=""
                              width="32"
                              height="32"
                            />
                          </div>
                          <span
                            style={{ marginLeft: "14px", whiteSpace: "nowrap" }}
                          >
                            {e.userNickname}
                          </span>

                          <div>
                            <div style={{ marginLeft: "14px" }}>
                              {e.communityCommentDesc}
                            </div>
                          </div>
                        </div>
                      ) : (
                        <div className="replysInfo">
                          <div style={{ borderRadius: "70%" }}>
                            <img
                              src={e.userProfileImg}
                              alt=""
                              width="32"
                              height="32"
                            />
                          </div>
                          <span
                            style={{ marginLeft: "14px", whiteSpace: "nowrap" }}
                          >
                            {e.userNickname}
                          </span>

                          <div>
                            <div style={{ marginLeft: "14px" }}>
                              {e.communityCommentDesc}
                              {e.userNickname ===
                                sessionStorage.getItem("userNickname") && (
                                <button
                                  style={{ color: "red" }}
                                  onClick={() => {
                                    deleteComment(e.communityCommentSeq);
                                  }}
                                >
                                  ??????
                                </button>
                              )}
                            </div>
                            <div style={{ margin: "10px 0 0 14px" }}>
                              <span>
                                {TimeCounting(
                                  e.communityCommentRegedAt,
                                  option
                                )}
                              </span>
                              <span
                                style={{ marginLeft: "15px" }}
                                onClick={() =>
                                  tagUser(e.userNickname, e.communityCommentSeq)
                                }
                              >
                                ?????? ??????
                              </span>
                            </div>
                            <div
                              style={{
                                display: "flex",
                                margin: "10px 0 10px 14px",
                              }}
                            >
                              <div
                                style={{
                                  borderBottom: "2px solid #e3e3e3",
                                  width: "40px",
                                  height: "13px",
                                }}
                              ></div>
                              {e.communityCommentLayerCnt > 0 ? (
                                <>
                                  {open == e.communityCommentSeq ? (
                                    <span
                                      style={{ marginLeft: "15px" }}
                                      onClick={() => {
                                        getReplyComments(e.communityCommentSeq);
                                        toggle(e.communityCommentSeq);
                                      }}
                                    >
                                      ?????? ?????????
                                    </span>
                                  ) : (
                                    <span
                                      style={{ marginLeft: "15px" }}
                                      onClick={() => {
                                        getReplyComments(e.communityCommentSeq);
                                        toggle(e.communityCommentSeq);
                                      }}
                                    >
                                      ?????? ??????({e.communityCommentLayerCnt}
                                      ???)
                                    </span>
                                  )}
                                </>
                              ) : (
                                <></>
                              )}
                            </div>
                            {open === e.communityCommentSeq ? (
                              <div style={{ margin: "12px 0 12px 14px" }}>
                                <div>
                                  {replyData.map((ele: any) => {
                                    return (
                                      <div
                                        style={{
                                          display: "flex",
                                          margin: "20px 0",
                                        }}
                                      >
                                        <div
                                          style={{
                                            borderRadius: "70%",
                                          }}
                                        >
                                          <img
                                            src={ele.userProfileImg}
                                            alt=""
                                            width="32"
                                            height="32"
                                          />
                                        </div>
                                        <div
                                          style={{
                                            margin: "0px 10px",
                                            whiteSpace: "nowrap",
                                          }}
                                        >
                                          {ele.userNickname}
                                        </div>
                                        {!ele.communityCommentIsDelete && (
                                          <div
                                            style={{
                                              color: "rgb(11 122 227)",
                                              whiteSpace: "nowrap",
                                            }}
                                          >
                                            @{e.userNickname}
                                          </div>
                                        )}
                                        <div
                                          style={{
                                            marginLeft: "7px",
                                          }}
                                        >
                                          {ele.communityCommentDesc}
                                          {ele.communityCommentIsDelete ? (
                                            <></>
                                          ) : (
                                            <>
                                              {" "}
                                              {ele.userNickname ===
                                                sessionStorage.getItem(
                                                  "userNickname"
                                                ) && (
                                                <button
                                                  style={{ color: "red" }}
                                                  onClick={() => {
                                                    deleteComment(
                                                      ele.communityCommentSeq
                                                    );
                                                  }}
                                                >
                                                  ??????
                                                </button>
                                              )}
                                            </>
                                          )}
                                        </div>
                                      </div>
                                    );
                                  })}
                                </div>
                              </div>
                            ) : null}
                          </div>
                        </div>
                      )}

                      <div></div>
                    </div>
                  );
                })}
              </div>
            </div>

            <div
              style={{
                position: "absolute",
                width: "100%",
                bottom: "0px",
                borderTop: "1px solid #e3e3e3",
                height: "50px",
                paddingRight: "16px",
                paddingLeft: "16px",
              }}
            >
              <div className="inputBox">
                {tagName && (
                  <div
                    style={{
                      color: "rgb(11 122 227)",
                      textAlign: "left",
                      width: "100%",
                    }}
                  >
                    {tagName}
                    <button
                      onClick={() => {
                        setTagName("");
                        setCommunityCommentLayer(1);
                      }}
                    >
                      x
                    </button>
                  </div>
                )}

                <input
                  type="text"
                  value={inputVal}
                  autoFocus={true}
                  spellCheck={false}
                  placeholder="????????? ???????????????"
                  onChange={(e) => {
                    onChangeText(e.target.value);
                  }}
                  onKeyPress={(e) => {
                    if (e.code === "Enter") createComment();
                  }}
                />
                <Button />
              </div>
            </div>
          </div>
        </Wrapper>
      </Box>
    </Modal>
  );
}
