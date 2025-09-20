    function initEditBooking() {
        const dropZone = document.getElementById('dropZone');
        const fileInput = document.getElementById('bookingImages');
        const previewContainer = document.getElementById('imagePreviews');
        const maxFiles = 5;
        let primaryImageIndex = 0;

        // Click on drop zone to trigger file input
        dropZone.addEventListener('click', function() {
            fileInput.click();
        });

        // Drag and drop functionality
        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            dropZone.addEventListener(eventName, preventDefaults, false);
        });

        function preventDefaults(e) {
            e.preventDefault();
            e.stopPropagation();
        }

        ['dragenter', 'dragover'].forEach(eventName => {
            dropZone.addEventListener(eventName, highlight, false);
        });

        ['dragleave', 'drop'].forEach(eventName => {
            dropZone.addEventListener(eventName, unhighlight, false);
        });

        function highlight() {
            dropZone.classList.add('dragover');
        }

        function unhighlight() {
            dropZone.classList.remove('dragover');
        }

        dropZone.addEventListener('drop', handleDrop, false);

        function handleDrop(e) {
            const dt = e.dataTransfer;
            const files = dt.files;
            handleFiles(files);
        }

        // File input change event
        fileInput.addEventListener('change', function() {
            handleFiles(this.files);
        });

        function handleFiles(files) {
            if (files.length === 0) return;

            const currentFileCount = previewContainer.children.length;
            const remainingSlots = maxFiles - currentFileCount;

            if (remainingSlots <= 0) {
                alert(`You can only upload up to ${maxFiles} images.`);
                return;
            }

            const filesToProcess = Array.from(files).slice(0, remainingSlots);

            filesToProcess.forEach(file => {
                if (!file.type.match('image.*')) {
                    alert('Please upload only image files.');
                    return;
                }

                if (file.size > 2 * 1024 * 1024) {
                    alert('File size exceeds 2MB. Please choose a smaller file.');
                    return;
                }

                const reader = new FileReader();
                reader.onload = function(e) {
                    addImagePreview(e.target.result, file.name);
                }
                reader.readAsDataURL(file);
            });
        }

        function addImagePreview(src, filename) {
            const previewDiv = document.createElement('div');
            previewDiv.className = 'image-preview';

            const img = document.createElement('img');
            img.src = src;
            img.alt = filename;

            const removeBtn = document.createElement('div');
            removeBtn.className = 'remove-btn';
            removeBtn.innerHTML = '×';
            removeBtn.onclick = function() {
                previewDiv.remove();
                updatePrimaryBadges();
            };

            const primaryBadge = document.createElement('div');
            primaryBadge.className = 'primary-badge';
            primaryBadge.innerHTML = 'Primary';
            primaryBadge.style.display = 'none';

            const setPrimaryBtn = document.createElement('div');
            setPrimaryBtn.className = 'set-primary';
            setPrimaryBtn.innerHTML = 'Set as Primary';
            setPrimaryBtn.onclick = function() {
                setAsPrimary(previewDiv);
            };

            previewDiv.appendChild(img);
            previewDiv.appendChild(removeBtn);
            previewDiv.appendChild(primaryBadge);
            previewDiv.appendChild(setPrimaryBtn);

            // If this is the first image, set it as primary
            if (previewContainer.children.length === 0) {
                setAsPrimary(previewDiv);
            }

            previewContainer.appendChild(previewDiv);
            updatePrimaryBadges();
        }

        function setAsPrimary(previewDiv) {
            // Remove primary status from all images
            const allPreviews = previewContainer.querySelectorAll('.image-preview');
            allPreviews.forEach(preview => {
                preview.querySelector('.primary-badge').style.display = 'none';
            });

            // Set this image as primary
            previewDiv.querySelector('.primary-badge').style.display = 'block';

            // Move this image to the first position
            previewContainer.insertBefore(previewDiv, previewContainer.firstChild);
        }

        function updatePrimaryBadges() {
            const allPreviews = previewContainer.querySelectorAll('.image-preview');
            if (allPreviews.length > 0) {
                allPreviews[0].querySelector('.primary-badge').style.display = 'block';
            }
        }

        // Form submission handling
        document.getElementById('saveBookingBtn').addEventListener('click', function() {
            // Collect image data for submission
            const imagePreviews = document.querySelectorAll('.image-preview img');
            const imageData = Array.from(imagePreviews).map(img => img.src);

            // You would typically send this data to your server here
            console.log('booking images:', imageData);

            // Continue with your existing form submission logic
            alert('booking saved with ' + imageData.length + ' images!');
        });
    }

