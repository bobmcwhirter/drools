package org.drools.task.service;

import javax.persistence.Query;

public enum CommandName {
    AddTaskRequest,
    AddAttachment,
    AddComment,
    
    Query_TasksOwned,
    
    Query_TasksAssignedAsBusinessAdministrator,
    Query_TasksAssignedAsExcludedOwner,
    Query_TasksAssignedAsPotentialOwner,
    Query_TasksAssignedAsRecipient,
    Query_TasksAssignedAsTaskInitiator,
    Query_TasksAssignedAsTaskStakeholder,
    
    Query_TaskSummaryResponse;
}
