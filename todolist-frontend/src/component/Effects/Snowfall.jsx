import React from "react";
import { motion } from "framer-motion";

const snowflakes = Array.from({ length: 40 });

export default function Snowfall() {
  return (
    <div className="pointer-events-none fixed inset-0 overflow-hidden z-[9999]">
      {snowflakes.map((_, i) => {
        const size = Math.random() * 8 + 4; // size 4–12px
        const left = Math.random() * 100; // random start
        const delay = Math.random() * 5; // random delay
        const duration = Math.random() * 5 + 5; // fall 6–10s

        return (
          <motion.div
            key={i}
            initial={{ y: -100, x: 0, opacity: 0 }}
            animate={{
              y: "110vh",
              x: [0, Math.random() * 80 - 40], // sway left-right
              opacity: [0, 1, 1, 0],
            }}
            transition={{
              duration: duration,
              delay: delay,
              repeat: Infinity,
              ease: "easeInOut",
            }}
            style={{
              position: "absolute",
              top: "-20px",
              left: `${left}%`,
              width: size,
              height: size,
              background: "white",
              borderRadius: "50%",
              filter: "blur(1px)",
            }}
          />
        );
      })}
    </div>
  );
}
