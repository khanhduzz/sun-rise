// Call the dataTables jQuery plugin
$(document).ready(function () {
  $('#dataTable').DataTable({
    "language": {
      "search": "Tìm kiếm:",
      "sLengthMenu": "Hiển thị _MENU_ bản ghi",
      "sInfo": "Hiển thị từ _START_ đến _END_ trên _TOTAL_ bản ghi",
      "emptyTable": "Không có bản ghi nào có sẵn"

    }
  });
});
