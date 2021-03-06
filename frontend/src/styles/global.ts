import { createGlobalStyle } from "styled-components";
const GlobalStyle = createGlobalStyle`
  body {
    font-family: 'Open Sans', 'Noto Sans KR' ,sans-serif;
    box-sizing: border-box;
    margin: 0;
    padding: 0;
    background-color: #fff;
    width: 100%;
    height: 100%;
    background-size: cover;   
    color: #333;
    overflow-x:hidden;
    
}
  a {
    text-decoration: none;
    color: inherit;
  }

  button {
    font-family: 'Open Sans', 'Noto Sans KR' ,sans-serif;
    background: transparent;
    border: none;
    cursor: pointer;
    color: #333;
  }

  ul {
    padding: 0;
    margin: 0;
  }
  html{
    scroll-behavior: smooth;
  }
  li {
    list-style-type : none ;
  }
  input:focus {outline:none;}
  textarea:focus {outline:none;}

  textarea {
    padding: 10px !important;
    resize: none;
  }

`;
export default GlobalStyle;
