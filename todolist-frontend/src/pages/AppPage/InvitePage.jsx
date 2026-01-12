import { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "sonner";
import {https_taskflow} from "../../service/api";

const getAccessToken = () => {
    return localStorage.getItem("USER_INFO");
};

export const verifyInvite = async (token) => {
    const accessToken = JSON.parse(localStorage.getItem("USER_INFO"))?.token;
    // hoặc localStorage.getItem("ACCESS_TOKEN")

    const res = await https_taskflow.post(
        "/v1/invites/verify",
        null,
        {
            params: { token },
            headers: {
                Authorization: `Bearer ${accessToken}`,
            },
        }
    );

    return res.status === 200;
};

export default function InvitePage() {
    const { token } = useParams();
    const navigate = useNavigate();

    useEffect(() => {

        console.log(token);

        if (!token) {
            navigate("/");
            return;
        }

        const accessToken = getAccessToken();

        // ❌ Chưa login
        if (!accessToken) {
            localStorage.setItem("invite_token", token);
            navigate("/login");
            return;
        }

        // ✅ Đã login → verify
        verifyInvite(token)
            .then(() => {
                toast.success("Bạn đã được mời vào project 🎉");
                localStorage.removeItem("invite_token");
                navigate("/app/upcoming");
            })
            .catch((err) => {
                toast.error(err?.response?.data?.message || "Link mời không hợp lệ");
                navigate("/");
            });
    }, [token, navigate]);

    return null; // hoặc Spinner
}
