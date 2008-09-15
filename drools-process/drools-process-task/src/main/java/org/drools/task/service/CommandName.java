package org.drools.task.service;


public enum CommandName {   
    ClaimRequest,
    ClaimResponse,
    StartRequest,
    StartResponse,
    StopRequest,
    StopResponse,
    ReleaseRequest,
    ReleaseResponse,    
    SuspendRequest,
    SuspendResponse,    
    ResumeRequest,
    ResumeResponse,    
    SkipRequest,
    SkipResponse,       
    CompleteRequest,
    CompleteResponse,   
    FailRequest,
    FailResponse,
    
    GetTaskRequest,
    GetTaskResponse,
    
    AddTaskRequest,
    AddTaskResponse,
    
    AddAttachmentRequest,
    AddAttachmentResponse,
    DeleteAttachmentRequest,
    DeleteAttachmentResponse,    
    SetDocumentContentRequest,
    SetDocumentContentResponse,
    GetContentRequest,
    GetContentResponse,
    
    AddCommentRequest,
    AddCommentResponse,
    DeleteCommentRequest,    
    DeleteCommentResponse,    
    
    QueryTasksOwned,    
    QueryTasksAssignedAsBusinessAdministrator,
    QueryTasksAssignedAsExcludedOwner,
    QueryTasksAssignedAsPotentialOwner,
    QueryTasksAssignedAsRecipient,
    QueryTasksAssignedAsTaskInitiator,
    QueryTasksAssignedAsTaskStakeholder,    
    QueryTaskSummaryResponse,
    
    RegisterForEventRequest,
    EventTriggerResponse,
    
    RegisterClient;
}
