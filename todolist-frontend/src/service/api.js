import axios from "axios";
import { store } from "..";

// axios instance
export let https_auth = axios.create({
  baseURL: "http://localhost:9999/api/auth",
});
export const https_authupdate = axios.create({
  baseURL: "http://localhost:9999/api/auth",
});

// luôn lấy token mới nhất từ localStorage khi gọi API
https_authupdate.interceptors.request.use((config) => {
  const token = JSON.parse(localStorage.getItem("USER_INFO"))?.token;

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export let https_taskflow = axios.create({
  baseURL: "http://localhost:8082/api/taskflow",
  headers: {
    Authorization:
      "Bearer " + JSON.parse(localStorage.getItem("USER_INFO"))?.token,
  },
});
export let https_user = axios.create({
  baseURL: "http://localhost:8081/api/user",
  headers: {
    Authorization:
      "Bearer " + JSON.parse(localStorage.getItem("USER_INFO"))?.token,
  },
});
// // Add a request interceptor
// https.interceptors.request.use(
//   function (config) {
//     store.dispatch(setLoadingOn());
//     // Do something before request is sent
//     return config;
//   },
//   function (error) {
//     // Do something with request error
//     return Promise.reject(error);
//   }
// );

// // Add a response interceptor
// https.interceptors.response.use(
//   function (response) {
//     store.dispatch(setLoadingOff());
//     // Any status code that lie within the range of 2xx cause this function to trigger
//     // Do something with response data
//     return response;
//   },
//   function (error) {
//     store.dispatch(setLoadingOff());
//     // Any status codes that falls outside the range of 2xx cause this function to trigger
//     // Do something with response error
//     return Promise.reject(error);
//   }
// );
