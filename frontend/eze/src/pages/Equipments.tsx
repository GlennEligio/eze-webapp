import { MouseEventHandler, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { IRootState } from "../store";
import { Equipment } from "../api/EquipmentService";
import * as EquipmentService from "../api/EquipmentService";
import useHttp from "../hooks/useHttp";
import EquipmentItem from "../components/Equipment/EquipmentItem";
import EquipmentModal from "../components/Equipment/EquipmentModal";

function Equipments() {
  const {
    sendRequest: getEquipments,
    data: equipments,
    error,
    status,
  } = useHttp<Equipment[]>(EquipmentService.getEquipments, false);
  const auth = useSelector((state: IRootState) => state.auth);
  const navigate = useNavigate();
  const backBtnHandler: MouseEventHandler = () => {
    navigate("/");
  };
  const [currentTime, setCurrentTime] = useState<Date>(new Date());

  useEffect(() => {
    setTimeout(() => {
      const newTime = new Date();
      setCurrentTime(newTime);
    }, 1000);
  }, [currentTime]);

  useEffect(() => {
    getEquipments({
      requestBody: null,
      requestConfig: { jwt: auth.accessToken },
    });
  }, []);

  return (
    <>
      <div className="container-md d-flex flex-column h-100">
        <div className="row">
          <header>
            <div className="pt-5 pb-2">
              <div className="d-flex justify-content-between">
                <div className="my-auto">
                  <span>
                    <i
                      className={`bi bi-arrow-left-circle fs-1 me-4 back-button`}
                      onClick={backBtnHandler}
                    ></i>
                  </span>
                  <i className="bi bi-gear fs-1"></i>
                </div>
                <div className="d-flex justify-content-end">
                  <div className="d-flex align-items-center">
                    <i className="bi bi-tools fs-1"></i>
                  </div>
                  <div className="d-flex flex-column justify-content-center ms-3 flex-grow-1">
                    <span className="fs-3">Equipment List</span>
                  </div>
                </div>
              </div>
            </div>
          </header>
        </div>
        <div className="row h-80">
          <main className="col-12 d-flex flex-column h-100">
            <div className="row mt-2 gx-1">
              <div className="col d-flex align-items-center justify-content-end">
                <div className="d-flex align-items-center px-2">
                  <i className="bi bi-arrow-repeat fs-4 me-1"></i>
                </div>
                <button
                  className="d-flex align-items-center px-2 ms-2 btn btn-sm btn-outline-dark"
                  data-bs-toggle="modal"
                  data-bs-target="#equipmentModal"
                >
                  <i className="bi bi-plus-circle fs-4 me-1"></i>
                  <span>Add</span>
                </button>
                <button className="d-flex align-items-center px-2 ms-2 btn btn-sm btn-outline-dark">
                  <i className="bi bi-pencil fs-4 me-1"></i>
                  <span>Edit</span>
                </button>
                <button className="d-flex align-items-center px-2 ms-2 btn btn-sm btn-outline-dark">
                  <i className="bi bi-trash fs-4 me-1"></i>
                  <span>Delete</span>
                </button>
              </div>
            </div>
            <div className="row mt-2 gx-0 overflow-auto">
              <div className="col">
                <div className="table-responsive-xxl">
                  <table
                    className="table table-hover"
                    style={{ minWidth: "1300px" }}
                  >
                    <thead className="table-dark">
                      <tr>
                        <th>Id</th>
                        <th>Equipment Code</th>
                        <th>Name</th>
                        <th>Status</th>
                        <th>Defective Since</th>
                        <th>Duplicable?</th>
                        <th>Borrowed?</th>
                        <th>Barcode</th>
                      </tr>
                    </thead>
                    <tbody>
                      {equipments != null &&
                        equipments.map((eq) => {
                          return (
                            <EquipmentItem
                              key={eq.id}
                              equipment={eq}
                            ></EquipmentItem>
                          );
                        })}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
            <div className="row py-3 mt-auto">
              <div className="col d-flex justify-content-between align-items-center">
                <div>
                  <h5>List of Equipments: {equipments?.length}</h5>
                </div>
                <div className="d-flex flex-column justify-content-center align-items-end">
                  <span>1:46 AM</span>
                  <span>12 Oct 2019</span>
                </div>
              </div>
            </div>
          </main>
        </div>
      </div>
      <EquipmentModal action="ADD" />
    </>
  );
}

export default Equipments;
