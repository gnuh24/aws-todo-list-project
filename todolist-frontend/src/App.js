import logo from "./logo.svg";
import "./App.css";
import { BrowserRouter, Outlet, Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage/HomePage";
import Layout from "./layout/Layout";
import RegisterPage from "./pages/RegisterPage/RegisterPage";
import LoginPage from "./pages/LoginPage/LoginPage";
import ForgotPassword from "./pages/ForgotPassword/ForgotPassword";
import Spinner from "./component/Spinner/Spinner";
import ResetPassword from "./pages/ResetPassword/ResetPassword";
import ForgotPasswordMail from "./pages/ForgotPasswordMail/ForgotPasswordMail";
import { GoogleOAuthProvider } from "@react-oauth/google";

import VerifyAccount from "./pages/RegisterPage/VerifyAccount";
import InboxPage from "./pages/AppPage/InboxPage";
import TodayPage from "./pages/AppPage/TodayPage";
import FiltersPage from "./pages/AppPage/FiltersPage";
import ProjectPage from "./pages/AppPage/ProjectPage";
import UpcomingPage from "./pages/AppPage/UpcomingPage";
import CompletedPage from "./pages/AppPage/CompletedPage";
import TestAPI from "./service/TestAPI";
import LoginSuccess from "./pages/LoginPage/LoginSuccess";
import SecureGate from "./layout/SecureGate";
import { Toaster } from "sonner";
import ArchivePage from "./pages/AppPage/ArchivePage";
import UnArchivePage from "./pages/AppPage/UnArchivePgae";
import NotificationsPage from "./pages/AppPage/NotificationPage";
import MainLayout from "./layout/MainLayout";
import NotFound from "./pages/AppPage/NotFound";
function App() {
  return (
    <>
      <Toaster position="top-right" richColors closeButton duration={2500} />
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
            <Route
                path="/app"
                element={
                  // <SecureGate>
                  // </SecureGate>
                    <MainLayout />
                }
            >
              <Route path="inbox" element={<InboxPage />} />
              <Route path="today" element={<TodayPage />} />
              <Route path="filters" element={<FiltersPage />} />

              <Route path="projects/:projectName/:projectId" element={<ProjectPage />} />
              <Route path="archive/:projectName/:projectId" element={<UnArchivePage />} />
              <Route path="upcoming" element={<UpcomingPage />} />
              <Route path="activity" element={<CompletedPage />} />
              <Route path="archive" element={<ArchivePage />} />

              {/* Notifications nằm trong MainLayout (có context) */}
              <Route path="notifications" element={<NotificationsPage />} />
            </Route>
            <Route path="testapi" element={<TestAPI></TestAPI>}></Route>
            <Route path="/login-success" element={<LoginSuccess />} />
            <Route path="*" element={<NotFound />} />

          </Routes>
          {/*<Route path="/app" element={<MainLayout></MainLayout>}>*/}
          {/*  <Route*/}
          {/*      path="notifications"*/}
          {/*      element={<NotificationsPage></NotificationsPage>}*/}
          {/*  ></Route>*/}
          {/*</Route>*/}
        </BrowserRouter>
      </GoogleOAuthProvider>
    </>
  );
}

export default App;
