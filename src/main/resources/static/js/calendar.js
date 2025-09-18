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

      // Generate availability calendar
      const calendar = document.getElementById('availability-calendar');
      const today = new Date();
      const currentMonth = today.getMonth();
      const currentYear = today.getFullYear();

      // Create calendar header
      const monthNames = ["January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"];

      const calendarHeader = document.createElement('div');
      calendarHeader.className = 'd-flex justify-content-between align-items-center mb-3';
      calendarHeader.innerHTML = `
          <h4 class="mb-0">${monthNames[currentMonth]} ${currentYear}</h4>
          <div>
              <button class="btn btn-sm btn-outline-secondary me-2" id="prev-month"><i class="fas fa-chevron-left"></i></button>
              <button class="btn btn-sm btn-outline-secondary" id="next-month"><i class="fas fa-chevron-right"></i></button>
          </div>
      `;
      calendar.appendChild(calendarHeader);

      // Create calendar days grid
      const daysGrid = document.createElement('div');
      daysGrid.className = 'row row-cols-7 g-1';

      // Add day headers
      const dayNames = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
      dayNames.forEach(day => {
          const dayElement = document.createElement('div');
          dayElement.className = 'col text-center fw-bold';
          dayElement.textContent = day;
          daysGrid.appendChild(dayElement);
      });

      // Get first day of month and number of days
      const firstDay = new Date(currentYear, currentMonth, 1).getDay();
      const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();

      // Add empty cells for days before the first day of the month
      for (let i = 0; i < firstDay; i++) {
          const emptyCell = document.createElement('div');
          emptyCell.className = 'col';
          daysGrid.appendChild(emptyCell);
      }

      // Add cells for each day of the month
      for (let i = 1; i <= daysInMonth; i++) {
          const dayCell = document.createElement('div');
          dayCell.className = 'col text-center p-2';

          const dateStr = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
          const isAvailable = availableDates.includes(dateStr);
          const isPast = new Date(dateStr) < new Date().setHours(0, 0, 0, 0);

          if (isPast) {
              dayCell.innerHTML = `<div class="unavailable-date py-1 rounded">${i}</div>`;
          } else if (isAvailable) {
              dayCell.innerHTML = `<div class="available-date py-1 rounded">${i}</div>`;
          } else {
              dayCell.innerHTML = `<div class="unavailable-date py-1 rounded">${i}</div>`;
          }

          daysGrid.appendChild(dayCell);
      }

      calendar.appendChild(daysGrid);

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