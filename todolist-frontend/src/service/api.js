import axios from "axios";
import { store } from "..";

export const BASE_URL = "https://sgutodolist.com/api";

// axios instance
export let https_auth = axios.create({
  baseURL: BASE_URL+"/auth",
  headers: {
    Authorization:
      "Bearer " + JSON.parse(localStorage.getItem("USER_INFO"))?.token
  },
});
export let https_taskflow = axios.create({
  baseURL: BASE_URL+"/taskflow",
  headers: {
    Authorization:
      "Bearer " + JSON.parse(localStorage.getItem("USER_INFO"))?.token
  },
});


export let https_notification = axios.create({
  baseURL: BASE_URL+"/notification",
  headers: {
    Authorization:
        "Bearer " + JSON.parse(localStorage.getItem("USER_INFO"))?.token
  },
});

export let https_user = axios.create({
  baseURL: BASE_URL+"/user",
  headers: {
    Authorization:
        "Bearer " + JSON.parse(localStorage.getItem("USER_INFO"))?.token
  },
});

export let https_model = axios.create({
  baseURL: BASE_URL+"/model",
  headers: {
    Authorization:
        "Bearer " + JSON.parse(localStorage.getItem("USER_INFO"))?.token
  },
});

export const https_authupdate = axios.create({
  baseURL: BASE_URL+"/auth",
});

// luôn lấy token mới nhất từ localStorage khi gọi API
https_authupdate.interceptors.request.use((config) => {
  const token = JSON.parse(localStorage.getItem("USER_INFO"))?.token;

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
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
