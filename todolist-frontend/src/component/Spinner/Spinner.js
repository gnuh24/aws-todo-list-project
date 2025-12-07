import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { ClimbingBoxLoader } from "react-spinners";
import { setLoadingOn, setLoadingOff } from "../../redux/SpinnerSlice";

export default function Spinner() {
  const { isLoading } = useSelector((state) => state.spinnerSlice);
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(setLoadingOn());
    const timer = setTimeout(() => {
      dispatch(setLoadingOff());
    }, 1500);

    return () => clearTimeout(timer);
  }, [dispatch]);

  return isLoading ? (
    <div
      style={{
        width: "100vw",
        height: "100vh",
        background: "white", // TODOIST STYLE
        position: "fixed",
        inset: 0,
        zIndex: 2000,
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <ClimbingBoxLoader
        size={22} // nhỏ gọn tinh tế như Todoist
        color="#e44232" // màu đỏ Todoist
        speedMultiplier={1.4} // nhẹ nhàng không quá nhanh
      />
    </div>
  ) : null;
}
