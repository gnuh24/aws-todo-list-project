import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  isLoading: false,
};

const SpinnerSlice = createSlice({
  name: "spinnerslice",
  initialState,
  reducers: {
    setLoadingOn: (state, action) => {
      state.isLoading = true;
    },
    setLoadingOff: (state, action) => {
      state.isLoading = false;
    },
  },
});

export const { setLoadingOn, setLoadingOff } = SpinnerSlice.actions;

export default SpinnerSlice.reducer;
