import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  isLoading: true, // Quan trọng: KHÔNG load khi vừa vào app
};

const SpinnerSlice = createSlice({
  name: "spinnerslice",
  initialState,
  reducers: {
    setLoadingOn: (state) => {
      state.isLoading = true;
    },
    setLoadingOff: (state) => {
      state.isLoading = false;
    },
  },
});

export const { setLoadingOn, setLoadingOff } = SpinnerSlice.actions;

export default SpinnerSlice.reducer;
