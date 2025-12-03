import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { PacmanLoader } from "react-spinners";
import { setLoadingOn, setLoadingOff } from "../../redux/SpinnerSlice";

export default function Spinner() {
  const { isLoading } = useSelector((state) => state.spinnerSlice);
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(setLoadingOn());
    const timer = setTimeout(() => {
      dispatch(setLoadingOff());
    }, 150); // 2s

    return () => clearTimeout(timer);
  }, [dispatch]);

  return isLoading ? (
    <div
      style={{
        width: "100vw",
        height: "100vh",
        background: "black",
        position: "fixed",
        top: 0,
        left: 0,
        zIndex: 2,
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <PacmanLoader size={150} color="#fca311" speedMultiplier={3} />
    </div>
  ) : null;
}
