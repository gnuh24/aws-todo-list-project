import logo from "./logo.svg";
import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage/HomePage";
import Layout from "./layout/Layout";
import RegisterPage from "./pages/RegisterPage/RegisterPage";
import LoginPage from "./pages/LoginPage/LoginPage";
import ForgotPassword from "./pages/ForgotPassword/ForgotPassword";
import Spinner from "./component/Spinner/Spinner";
import ResetPassword from "./pages/ResetPassword/ResetPassword";
import ForgotPasswordMail from "./pages/ForgotPasswordMail/ForgotPasswordMail";

function App() {
  return (
    <>
      <BrowserRouter>
        <Spinner></Spinner>
        <Routes>
          <Route path="/" element={<Layout></Layout>}>
            <Route path="/" element={<HomePage></HomePage>}></Route>
          </Route>
          <Route
            path="/register"
            element={<RegisterPage></RegisterPage>}
          ></Route>
          <Route path="/login" element={<LoginPage></LoginPage>}></Route>
          <Route
            path="/forgotpassword"
            element={<ForgotPassword></ForgotPassword>}
          ></Route>
          <Route
            path="/mailed"
            element={<ForgotPasswordMail></ForgotPasswordMail>}
          ></Route>
          <Route
            path="/ResetPassword"
            element={<ResetPassword></ResetPassword>}
          ></Route>
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
