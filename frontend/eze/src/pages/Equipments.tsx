import { MouseEventHandler, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { IRootState } from "../store";
import { Equipment } from "../api/EquipmentService";
import * as EquipmentService from "../api/EquipmentService";
import useHttp, { RequestConfig } from "../hooks/useHttp";
import EquipmentItem from "../components/Equipment/EquipmentItem";
import { equipmentActions } from "../store/equipmentSlice";
import { useDispatch } from "react-redux";
import AddEquipmentForm from "../components/UI/Modal/AddEquipmentModal";
import UpdateEquipmentForm from "../components/UI/Modal/UpdateEquipmentModal";
import DeleteEquipmentModal from "../components/UI/Modal/DeleteEquipmentModal";
import MiniClock from "../components/UI/Other/MiniClock";

function Equipments() {
  const equipment = useSelector((state: IRootState) => state.equipment);
  const [equipments, setEquipments] = useState([]);
  const auth = useSelector((state: IRootState) => state.auth);
  const dispatch = useDispatch();
  const {
    sendRequest: getEquipments,
    data,
    error,
    status,
  } = useHttp<Equipment[]>(EquipmentService.getEquipments, false);
  const navigate = useNavigate();
  const backBtnHandler: MouseEventHandler = () => {
    navigate("/");
  };

  // Fetch Equipments on mount from backend api
  useEffect(() => {
    fetchEquipments();
  }, []);

  // For setting the equipmentState based on data from useHttp
  useEffect(() => {
    if (status === "completed" && error === null) {
      dispatch(equipmentActions.saveEquipments({ equipments: data }));
    }
  }, [data]);

  // onClickHandler to update selected Equipment
  const onEqItemClickHandler = (selectedEquipment: Equipment) => {
    if (
      selectedEquipment.equipmentCode ===
      equipment.selectedEquipment?.equipmentCode
    ) {
      dispatch(
        equipmentActions.updateSelectedEquipment({ selectedEquipment: null })
      );
      return;
    }
    dispatch(
      equipmentActions.updateSelectedEquipment({
        selectedEquipment: selectedEquipment,
      })
    );
  };

  // sends request to fetch equipment
  const fetchEquipments = () => {
    const requestConfig: RequestConfig = {
      headers: {
        Authorization: `Bearer ${auth.accessToken}`,
      },
    };
    getEquipments(requestConfig);
  };

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
                <div
                  className="d-flex align-items-center px-2"
                  onClick={() => fetchEquipments()}
                >
                  <i className="bi bi-arrow-repeat fs-4 me-1"></i>
                </div>
                <button
                  className="d-flex align-items-center px-2 ms-2 btn btn-sm btn-dark"
                  data-bs-toggle="modal"
                  data-bs-target="#addEquipmentModal"
                >
                  <i className="bi bi-plus-circle fs-4 me-1"></i>
                  <span>Add</span>
                </button>
                <button
                  className="d-flex align-items-center px-2 ms-2 btn btn-sm btn-dark"
                  data-bs-toggle="modal"
                  data-bs-target="#updateEquipmentModal"
                  disabled={equipment.selectedEquipment == null}
                >
                  <i className="bi bi-pencil fs-4 me-1"></i>
                  <span>Edit</span>
                </button>
                <button
                  className="d-flex align-items-center px-2 ms-2 btn btn-sm btn-dark"
                  data-bs-toggle="modal"
                  data-bs-target="#deleteEquipmentModal"
                  disabled={equipment.selectedEquipment == null}
                >
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
                      {equipment.equipments != null &&
                        equipment.equipments.map((eq) => {
                          return (
                            <EquipmentItem
                              key={eq.id}
                              equipment={eq}
                              focused={
                                eq.equipmentCode ===
                                equipment.selectedEquipment?.equipmentCode
                              }
                              onUpdateSelectedEquipment={onEqItemClickHandler}
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
                  <h5>List of Equipments: {equipment.equipments?.length}</h5>
                </div>
                <MiniClock />
              </div>
            </div>
          </main>
        </div>
      </div>
      <div>
        <AddEquipmentForm />
        <UpdateEquipmentForm />
        <DeleteEquipmentModal />
      </div>
    </>
  );
}

export default Equipments;
