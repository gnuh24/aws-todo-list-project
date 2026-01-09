
export const CLIENT_ID =
    sessionStorage.getItem("CLIENT_ID") ||
    (() => {
        const id = crypto.randomUUID();
        sessionStorage.setItem("CLIENT_ID", id);
        return id;
    })();
