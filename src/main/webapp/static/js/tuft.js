angular.
    module("tuft", ["tuft-services", "tuft-directives"]).
    controller(
        "MainScreenController",
        [
            "$scope", "TuftService",
            function($scope, TuftService) {
                $scope.tufts = [];
                $scope.selectedTuft = {};
                $scope.editingTuft = {};
                TuftService.getTufts(function(tufts) {
                    $scope.tufts = tufts;
                });
                $scope.editTuft = function(tuft) {
                    $scope.editingTuft = tuft;
                };
            }
        ]
    ).
    controller(
        "ProfileScreenController",
        [
            "$scope", "ProfileService",
            function($scope, ProfileService) {

            }
        ]
    );