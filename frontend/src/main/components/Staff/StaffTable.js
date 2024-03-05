import React from "react";
import OurTable from "main/components/OurTable"
//  import { useBackendMutation } from "main/utils/useBackend";
//  import { cellToAxiosParamsDelete, onDeleteSuccess } from "main/components/Utils/StaffUtils"
//  import { useNavigate } from "react-router-dom";
//  import { hasRole } from "main/utils/currentUser";

 export default function StaffTable({ staff, currentUser }) {

    //  const navigate = useNavigate();

    //  const addCallback = (cell) => {
    //      navigate(`/staff/edit/${cell.row.values.id}`);
    //  };

     // Stryker disable all : hard to test for query caching

    //  const deleteMutation = useBackendMutation(
    //      cellToAxiosParamsDelete,
    //      { onSuccess: onDeleteSuccess },
    //      ["/api/courses/getStaff"]
    //  );
     // Stryker restore all 

     // Stryker disable next-line all : TODO try to make a good test for this
    //  const deleteCallback = async (cell) => { deleteMutation.mutate(cell); }

     const columns = [
         {
             Header: 'id',
             accessor: 'id',
         },
         {
             Header: 'courseId',
             accessor: 'courseId',
         },
         {
             Header: 'githubId',
             accessor: 'githubId',
         },
   
     ];

    //  if (hasRole(currentUser, "ROLE_ADMIN") || hasRole(currentUser, "ROLE_INSTRUCTOR")) {
    //     //  columns.push(ButtonColumn("Edit", "primary", editCallback, "CoursesTable"));
    //     //  columns.push(ButtonColumn("Delete", "danger", deleteCallback, "StaffTable"));
    //  }

     return <OurTable
         data={staff}
         columns={columns}
         testid={"StaffTable"} />;
    };