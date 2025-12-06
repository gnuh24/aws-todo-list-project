import { useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "sonner";
import { https_auth } from "../../service/api";
export default function ResetPassword() {
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [OTP, setOTP] = useState("");
  const handleSubmit = async (e) => {
    e.preventDefault();

    const emailUser = JSON.parse(localStorage.getItem("USER_VERIFY")); // Lấy email từ localStorage
    console.log("Email:", emailUser);
    console.log("OTP:", OTP);

    // Kiểm tra lại password khớp
    if (password !== confirmPassword) {
      toast.error("Passwords do not match!");
      return;
    }

    try {
      //  Gọi API reset password
      const response = await https_auth.patch(
        `/v1/reset-password/${emailUser}`,
        {
          otp: OTP,
          password: password,
        }
      );

      // Kiểm tra phản hồi
      if (response.status === 200) {
        console.log(" Reset password success:", response.data);
        toast.error("Đặt lại mật khẩu thành công!");
        localStorage.removeItem("USER_VERIFY"); // Xoá dữ liệu sau khi xong
        window.location.href = "/login"; // Chuyển hướng về trang đăng nhập
      }
    } catch (error) {
      console.error(" Reset password failed:", error.response?.data || error);
      toast.error(
        error.response?.data?.message ||
          "Đặt lại mật khẩu thất bại, vui lòng thử lại!"
      );
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-white p-6">
      <div className="w-full max-w-4xl grid grid-cols-1 md:grid-cols-2 shadow-lg rounded-2xl overflow-hidden border border-gray-200">
        {/* Left: Form */}
        <div className="p-8 flex flex-col justify-center">
          <Link to="/">
            <div className="mb-6">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="127"
                height="32"
                fill="none"
                viewBox="0 0 127 32"
              >
                <g clipPath="url(#todoist-extended-logo_svg__a)">
                  <path
                    fill="#E44232"
                    fillRule="evenodd"
                    d="M3.92 0h23.947c2.195 0 3.99 1.8 3.99 4v24c0 2.2-1.795 4-3.99 4H3.919c-2.195 0-3.99-1.8-3.99-4v-5.374l.056.033a944 944 0 0 0 5.37 3.12c.477.27.934.264 1.394-.002l6.224-3.59.116-.067.003-.002.018-.01.065-.038 6.496-3.746c.276-.16.29-.651-.02-.829l-.217-.123v-.001h-.001c-.316-.18-.724-.413-.898-.516a1.02 1.02 0 0 0-.995.012c-.155.09-10.479 6.039-10.824 6.235a1.34 1.34 0 0 1-1.336 0l-5.452-3.175v-2.693l.057.033c1.362.795 4.581 2.674 5.37 3.12.477.27.934.264 1.394-.002l6.353-3.664.024-.014.004-.002c3.188-1.84 6.4-3.691 6.54-3.773.277-.16.291-.651-.019-.828l-.217-.124c-.315-.18-.725-.414-.9-.517a1.02 1.02 0 0 0-.994.012c-.155.09-10.479 6.039-10.824 6.235a1.34 1.34 0 0 1-1.336 0l-5.452-3.174v-2.693l.056.032c1.361.795 4.582 2.674 5.37 3.12.478.27.935.264 1.395-.002l6.36-3.668.008-.005h.002l.08-.047L19.67 7.54c.276-.16.29-.65-.02-.828l-.217-.124c-.316-.18-.725-.414-.9-.516a1.02 1.02 0 0 0-.994.012c-.155.089-10.479 6.038-10.824 6.235a1.34 1.34 0 0 1-1.336 0L-.072 9.143V4c0-2.2 1.796-4 3.991-4"
                    clipRule="evenodd"
                  ></path>
                  <path
                    fill="#fff"
                    d="m6.749 14.993 6.36-3.668.008-.005c3.192-1.84 6.412-3.698 6.553-3.78.277-.16.29-.65-.019-.828l-.217-.124c-.316-.18-.725-.414-.9-.516a1.02 1.02 0 0 0-.995.012c-.154.089-10.478 6.038-10.823 6.235a1.34 1.34 0 0 1-1.336 0L-.072 9.143v2.698l.056.033c1.361.795 4.582 2.674 5.37 3.12.478.27.935.264 1.395-.002"
                  ></path>
                  <path
                    fill="#fff"
                    d="m6.749 20.385 6.352-3.664.024-.014 6.545-3.775c.277-.16.29-.651-.019-.828l-.217-.124c-.316-.18-.725-.414-.9-.517a1.02 1.02 0 0 0-.995.012c-.154.09-10.478 6.039-10.823 6.235a1.34 1.34 0 0 1-1.336 0l-5.452-3.174v2.698l.057.033c1.362.795 4.581 2.674 5.37 3.12.477.27.934.264 1.394-.002"
                  ></path>
                  <path
                    fill="#fff"
                    d="m13.11 22.108-6.361 3.669c-.46.265-.917.272-1.394.002a945 945 0 0 1-5.37-3.12l-.057-.033v-2.699l5.452 3.175c.412.239.923.235 1.336 0 .345-.196 10.669-6.146 10.823-6.235.372-.214.773-.142.995-.012.175.103.584.336.9.517l.217.123c.31.178.296.669.02.829z"
                  ></path>
                  <path
                    fill="#E44232"
                    fillRule="evenodd"
                    d="M55.528 19.082c0 .515.088 1.015.263 1.492q.261.72.773 1.273.51.554 1.228.884t1.627.331 1.628-.33a3.9 3.9 0 0 0 1.228-.885 3.8 3.8 0 0 0 .772-1.273q.26-.717.26-1.492 0-.774-.26-1.494a3.8 3.8 0 0 0-.772-1.271 3.9 3.9 0 0 0-1.228-.885 3.85 3.85 0 0 0-1.628-.333q-.91.001-1.627.333a3.9 3.9 0 0 0-1.229.885 3.7 3.7 0 0 0-.772 1.27q-.263.721-.263 1.495m-3.309 0q0-1.576.566-2.862a6.7 6.7 0 0 1 1.531-2.198 6.8 6.8 0 0 1 2.288-1.411 8 8 0 0 1 2.815-.498q1.489 0 2.813.498a6.8 6.8 0 0 1 2.29 1.41 6.7 6.7 0 0 1 1.531 2.199q.566 1.285.566 2.862 0 1.576-.566 2.862a6.7 6.7 0 0 1-1.53 2.198 6.75 6.75 0 0 1-2.29 1.409 7.9 7.9 0 0 1-2.814.497q-1.49.001-2.815-.497a6.7 6.7 0 0 1-2.288-1.409 6.7 6.7 0 0 1-1.53-2.198q-.567-1.286-.567-2.862M71.472 19.086q0 .774.263 1.491.26.722.772 1.274.51.553 1.228.884t1.627.33 1.628-.33a3.763 3.763 0 0 0 2-2.158q.26-.717.26-1.491t-.26-1.495a3.78 3.78 0 0 0-2-2.155 3.85 3.85 0 0 0-1.627-.333 3.85 3.85 0 0 0-1.628.333 3.9 3.9 0 0 0-1.228.884q-.51.554-.773 1.271-.262.722-.262 1.494m7.835 4.643h-.056q-.717 1.218-1.93 1.771a6.3 6.3 0 0 1-2.62.552q-1.545.001-2.747-.538a6.15 6.15 0 0 1-2.054-1.48 6.3 6.3 0 0 1-1.297-2.213 8.3 8.3 0 0 1-.44-2.736q-.002-1.465.469-2.738a6.8 6.8 0 0 1 1.309-2.212 6.1 6.1 0 0 1 2.027-1.478 6.2 6.2 0 0 1 2.593-.54q.939 0 1.657.193.716.194 1.268.5.552.305.938.648.387.347.634.677h.083v-7.31c0-.332.262-.722.722-.722h1.87c.432 0 .72.364.72.721v18.174c0 .461-.391.723-.72.723H80.03a.73.73 0 0 1-.723-.722zM87.963 19.082c0 .515.088 1.015.263 1.492q.261.72.773 1.273.51.554 1.228.884.717.33 1.627.331.91 0 1.628-.33.717-.332 1.228-.885.51-.553.772-1.273.26-.717.26-1.492 0-.774-.26-1.494a3.8 3.8 0 0 0-.772-1.271 3.9 3.9 0 0 0-1.228-.885 3.85 3.85 0 0 0-1.628-.333q-.91.001-1.627.333t-1.228.885q-.512.552-.773 1.27-.263.721-.263 1.495m-3.309 0q0-1.576.566-2.862a6.7 6.7 0 0 1 1.531-2.198 6.8 6.8 0 0 1 2.288-1.411 8 8 0 0 1 2.815-.498q1.49 0 2.813.498a6.8 6.8 0 0 1 2.29 1.41 6.7 6.7 0 0 1 1.532 2.199q.565 1.285.566 2.862 0 1.576-.567 2.862a6.7 6.7 0 0 1-1.53 2.198 6.75 6.75 0 0 1-2.29 1.409 7.9 7.9 0 0 1-2.814.497q-1.49.001-2.815-.497a6.7 6.7 0 0 1-2.288-1.409 6.7 6.7 0 0 1-1.53-2.198q-.567-1.286-.567-2.862M100.723 7.94q0-.802.592-1.398.597-.594 1.505-.594.91 0 1.531.568.62.566.622 1.424-.001.857-.622 1.424-.62.567-1.531.567-.908 0-1.505-.596-.592-.593-.592-1.395M114.388 16.123c-.33 0-.573-.25-.615-.36-.275-.723-1.152-.994-1.86-.994-1.117 0-1.993.518-1.993 1.41 0 .863.848 1.04 1.372 1.198.575.175 1.673.415 2.278.557a7.4 7.4 0 0 1 1.725.637c1.757.915 2.008 2.353 2.008 3.22 0 3.196-3.161 4.257-5.355 4.257-1.691 0-4.868-.257-5.566-3.488-.068-.315.21-.798.72-.798h1.828c.36 0 .602.263.672.47.235.649.982 1.139 2.24 1.139 1.351 0 2.149-.537 2.149-1.25 0-.462-.261-.872-.602-1.104-1.024-.696-3.556-.775-4.931-1.508-.526-.28-1.848-.922-1.848-3.11 0-3.014 2.735-4.286 5.137-4.286 3.544 0 4.844 2.244 4.991 3.076.081.459-.176.934-.691.934zM117.573 14.48v-1.372c0-.328.258-.721.716-.721h1.765v-3.37c0-.36.244-.58.429-.66.109-.049 1.097-.48 1.885-.825.551-.227.997.228.997.666v4.189h2.922c.451 0 .72.395.72.721v1.374a.744.744 0 0 1-.72.724h-2.922v5.807c0 .664-.018 1.182.235 1.566.232.351.572.481 1.254.481.196 0 .37-.032.518-.083a.7.7 0 0 1 .865.318c.216.418.463.877.635 1.206.19.361.038.824-.31.993-.56.273-1.336.494-2.4.494-.883 0-1.383-.097-1.941-.29a3.35 3.35 0 0 1-1.415-1c-.323-.396-.483-.926-.602-1.516-.121-.59-.15-1.305-.15-2.08v-5.896h-1.761c-.462 0-.72-.4-.72-.726M41.836 14.48v-1.372c0-.328.258-.721.715-.721h2.017v-3.37c0-.36.244-.58.429-.66l1.885-.825c.551-.227.997.228.997.666v4.189h2.922c.451 0 .72.395.72.721v1.374a.745.745 0 0 1-.72.724H47.88v5.807c0 .664-.018 1.182.235 1.566.232.351.572.481 1.254.481.196 0 .37-.032.518-.083a.704.704 0 0 1 .865.318c.216.418.463.877.636 1.206.19.361.037.824-.31.993-.561.273-1.337.494-2.401.494-.883 0-1.382-.097-1.941-.29a3.34 3.34 0 0 1-1.414-1c-.324-.396-.484-.926-.603-1.516-.121-.59-.15-1.305-.15-2.08v-5.896h-2.012c-.462 0-.72-.4-.72-.726M101.891 25.72h1.871c.396 0 .72-.324.72-.72V13.107a.723.723 0 0 0-.72-.722h-1.871a.723.723 0 0 0-.72.722V25c0 .397.324.722.72.722"
                    clipRule="evenodd"
                  ></path>
                </g>
                <defs>
                  <clipPath id="todoist-extended-logo_svg__a">
                    <path fill="#fff" d="M0 0h127v32H0z"></path>
                  </clipPath>
                </defs>
              </svg>
            </div>
          </Link>
          <h1 className="text-2xl font-bold mb-4">Password reset</h1>
          <p className="text-gray-600 mb-6 text-sm">
            Please enter a new password for your Todoist account. <br />
            This will end all active sessions for your account and issue a new
            API token.
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* OTP code */}
            <div className="relative">
              <input
                type={OTP}
                placeholder="Enter your OTP code"
                value={OTP}
                onChange={(e) => setOTP(e.target.value)}
                required
                // minLength={8}
                className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-400"
              />
            </div>

            {/* New password */}
            <div className="relative">
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Enter a new password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                minLength={8}
                className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-400"
              />
              <button
                type="button"
                className="absolute inset-y-0 right-3 flex items-center text-gray-500"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? "🙈" : "👁️"}
              </button>
            </div>

            {/* Confirm password */}
            <div className="relative">
              <input
                type={showConfirmPassword ? "text" : "password"}
                placeholder="Confirm your new password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                minLength={8}
                className="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-400"
              />
              <button
                type="button"
                className="absolute inset-y-0 right-3 flex items-center text-gray-500"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              >
                {showConfirmPassword ? "🙈" : "👁️"}
              </button>
            </div>

            <p className="text-xs text-gray-500">
              Your password must be at least 8 characters long. Avoid common
              words or patterns.
            </p>

            <button
              type="submit"
              className="w-full bg-red-500 hover:bg-red-600 text-white font-medium py-2 rounded-lg"
            >
              Reset my password
            </button>
          </form>

          <p className="text-xs text-gray-500 mt-6">
            Need additional help?{" "}
            <a href="#" className="underline text-gray-700">
              Contact us
            </a>
          </p>
        </div>

        {/* Right: Illustration */}
        <div className="hidden md:flex items-center justify-center bg-gray-50 p-6">
          <img
            src="https://todoist.b-cdn.net/assets/images/03db60e1fed9b4ba.png"
            alt="illustration"
            className="w-64"
          />
        </div>
      </div>
    </div>
  );
}
