import { useState, useEffect } from "react";

const MiniClock = () => {
  const [currentTime, setCurrentTime] = useState<Date>(new Date());
  const dateString = `${new Intl.DateTimeFormat("en-US", {
    day: "2-digit",
  }).format(currentTime)} ${new Intl.DateTimeFormat("en-US", {
    month: "short",
  }).format(currentTime)} ${new Intl.DateTimeFormat("en-US", {
    year: "numeric",
  }).format(currentTime)}`;

  // For updating time in footer
  useEffect(() => {
    setTimeout(() => {
      const newTime = new Date();
      setCurrentTime(newTime);
    }, 1000);
  }, [currentTime]);

  return (
    <div className="d-flex flex-column justify-content-center align-items-end">
      <span>
        {new Intl.DateTimeFormat("en-US", {
          hour: "numeric",
          minute: "numeric",
          hour12: true,
        }).format(currentTime)}
      </span>
      <span>{dateString}</span>
    </div>
  );
};

export default MiniClock;
