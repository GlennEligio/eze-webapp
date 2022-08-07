import { useEffect, useState } from "react";

const MenuClock = () => {
  const [currentTime, setCurrentTime] = useState<Date>(new Date());

  useEffect(() => {
    setTimeout(() => {
      const newTime = new Date();
      setCurrentTime(newTime);
    }, 1000);
  }, [currentTime]);

  return (
    <div className="row gx-0 flex-grow-1 border border-dark border-1 mt-2">
      <div className="col d-flex flex-column align-items-center justify-content-center">
        <p className="fs-4">
          {new Intl.DateTimeFormat("en-US", { weekday: "long" })
            .format(currentTime)
            .toUpperCase()}
        </p>
        <p className="text-danger" style={{ fontSize: "10rem" }}>
          {currentTime.getUTCDate()}
        </p>
        <p className="fs-4">{`${new Intl.DateTimeFormat("en-US", {
          month: "long",
        }).format(currentTime)}, ${currentTime.getFullYear()}`}</p>
        <h1 className="text-danger fs-4">
          {new Intl.DateTimeFormat("en-US", {
            hour: "numeric",
            minute: "numeric",
            second: "numeric",
            hour12: true,
          }).format(currentTime)}
        </h1>
      </div>
    </div>
  );
};

export default MenuClock;
