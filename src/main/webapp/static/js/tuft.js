angular.
    module("tuft", ["ngAnimate", "ui.bootstrap"]).
    controller(
        "NavigationController",
        [
            "$scope",
            function($scope) { }
        ]
    ).
    controller(
        "ConnectionManagementController",
        [
            "$scope",
            function($scope) {
                $scope.connectionState = "connected";
                $scope.connectButtonText = function() {
                    if($scope.connectionState === 'connected') {
                        return "Connected";
                    } else if($scope.connectionState === 'requested') {
                        return "Request Sent";
                    } else if($scope.connectionState === 'toAccept') {
                        return "Accept Request";
                    } else if($scope.connectionState === 'toRequest') {
                        return "Request Connection";
                    }
                    return "Invalid State";
                };
                $scope.connectWithUser = function() {
                    if($scope.connectionState === 'toRequest') {
                        requestConnection("angular.element($('#ConnectionManagementController')).scope().attemptConnection");
                    } else if ($scope.connectionState === 'toAccept') {
                        acceptConnection("angular.element($('#ConnectionManagementController')).scope().attemptAccept");
                    }
                };
                $scope.attemptConnection = function(result) {
                    if(result) {
                        $scope.connectionState = 'requested';
                        $scope.$apply();
                    } else {
                        alert("Failed to connect with user");
                    }
                };
                $scope.attemptAccept = function(result) {
                    if(result) {
                        $scope.connectionState = 'connected';
                        $scope.$apply();
                    } else {
                        alert("Failed to accept connection");
                    }
                };
                $scope.canClick = function() {
                    return $scope.connectionState === 'toAccept' || $scope.connectionState === 'toRequest';
                };
            }
        ]
    );