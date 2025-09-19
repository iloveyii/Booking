function getCalendar(availableDates, checkInDate, checkOutDate) {



      // Convert to Date objects
      const availableDateObjects = availableDates.map(date => new Date(date));

      // Initialize Flatpickr for check-in
      const checkInPicker = flatpickr(checkInDate, {
          minDate: "today",
          enable: availableDateObjects,
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
          enable: availableDateObjects
      });

      // Handle form submission
      document.getElementById('bookingForm').addEventListener('submit', function(e) {
          e.preventDefault();

          const checkIn = document.getElementById('checkInDate').value;
          const checkOut = document.getElementById('checkOutDate').value;
          const guests = document.getElementById('guests').value;

          if (!checkIn || !checkOut) {
              alert('Please select both check-in and check-out dates');
              return;
          }

          if (new Date(checkIn) >= new Date(checkOut)) {
              alert('Check-out date must be after check-in date');
              return;
          }

          // In a real application, you would submit the booking here
          alert(`Booking request received!\nCheck-in: ${checkIn}\nCheck-out: ${checkOut}\nGuests: ${guests}`);
      });


}