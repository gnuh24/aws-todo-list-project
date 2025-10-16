import { GoogleLogin } from "@react-oauth/google";
import { jwtDecode } from "jwt-decode";

export default function GoogleLoginButton() {
  return (
    <>
      <GoogleLogin
        onSuccess={(credentialResponse) => {
          console.log("Google Login Success:", credentialResponse);

          // Decode JWT để lấy user info
          const user = jwtDecode(credentialResponse.credential);
          console.log("User Info:", user);
        }}
        onError={() => {
          console.log("Login Failed");
        }}
      />
    </>
  );
}
