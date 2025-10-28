import axios from "axios";
import { store } from "..";

// axios instance
export let https_auth = axios.create({
  baseURL: "http://localhost:9999/api/auth",
  // headers: {
  //   TokenCybersoft:
  //     "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0ZW5Mb3AiOiJCb290Y2FtcCA2OCIsIkhldEhhblN0cmluZyI6IjE1LzAxLzIwMjUiLCJIZXRIYW5UaW1lIjoiMTczNjg5OTIwMDAwMCIsIm5iZiI6MTcwOTEzOTYwMCwiZXhwIjoxNzM3MDQ2ODAwfQ.15h8Zu___NIMHyUdFGA_OXmW8LeIiC8dEKnAv1v362Q",
  //   Authorization:
  //     "Bearer " + JSON.parse(localStorage.getItem("USER_INFO"))?.accessToken,
  // },
});
export let https_taskflow = axios.create({
  baseURL: "http://localhost:8082/api/taskflow",
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
