function getCalendar(availableDates, checkInDate, checkOutDate) {
      // Convert to Date objects
      const availableDateObjects = availableDates.map(date => new Date(date));

      // Initialize Flatpickr for check-in
      const checkInPicker = flatpickr(checkInDate, {
          minDate: "today",
          enable: availableDateObjects,
          defaultDate: availableDates[0],
          onChange: function(selectedDates, dateStr, instance) {
              if (selectedDates.length > 0) {
                  // When check-in is selected, update check-out to only allow dates after check-in
                  checkOutPicker.set('minDate', selectedDates[0]);
                  checkOutPicker.set('enable', availableDateObjects.filter(date => date > selectedDates[0]));

                  // If check-out is already selected and is before check-in, clear it
                  if (checkOutPicker.selectedDates.length > 0 && checkOutPicker.selectedDates[0] <= selectedDates[0]) {
                      checkOutPicker.clear();
                  }
              }
          }
      });

      // Initialize Flatpickr for check-out
      const checkOutPicker = flatpickr(checkOutDate, {
          minDate: "today",
          enable: availableDateObjects,
      });
}