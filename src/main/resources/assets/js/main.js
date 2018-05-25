$(document).ready(function () {

    $("#btn-new").click(function () {
        $(".modal-title").text("Register");
        $("#btn-submit").removeClass("hidden");
        $("#btn-save").addClass("hidden");
        $("#myModal").find("input").val("");
    })
});

function edit(thiz) {
    event.preventDefault();

    var url = '/pilots/get/' + $(thiz).parents(".card").find("#pilotId").val();

    $.ajax({
        url : url,
        type : 'GET',
        dataType : 'json',
        success : function(pilot) {
            if (pilot) {
                $("#btn-new").click();

                // Set value profile
                $("#pilotIdUpdate").val(pilot.id);
                $("#pilotName").val(pilot.name);
                $("#pilotInfo").val(pilot.info);
                $("#pilotLevel").val(pilot.level);

                // Control button
                $(".modal-title").text("Profile");
                $("#btn-submit").addClass("hidden");
                $("#btn-save").removeClass("hidden");
            }
        },
        error : function(jqHR, textStatus, errorThrown) {
            console.log(textStatus, errorThrown);
        }
    });
}