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
import { GoogleOAuthProvider } from "@react-oauth/google";
import TaskPage from "./pages/TaskPage/TaskPage";
import VerifyAccount from "./pages/RegisterPage/VerifyAccount";
function App() {
  return (
    <>
      <GoogleOAuthProvider clientId="100882842939-4lmau4i91h1b6q040efmnm7jun8fue2r.apps.googleusercontent.com">
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
            <Route path="/auth/verify-account" element={<VerifyAccount />} />
            <Route path="/Task" element={<TaskPage></TaskPage>}></Route>
          </Routes>
        </BrowserRouter>
      </GoogleOAuthProvider>
    </>
  );
}

export default App;
