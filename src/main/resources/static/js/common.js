// API request
async function makeApiRequest(httpMethod, endPoint, data = {}, prefix = true, secure = true) {
    // Make the request to /api/endpoint
    if(httpMethod === 'GET' && Object.keys(data).length > 0) {
        Object.keys(data).map( k => endPoint = endPoint + `/${data[k]}`)
    }
    const prefixed = prefix ? '/api/' : '';
    let headers = {
        'Content-Type': 'application/json'
    };
    if(secure) {
        headers = {
           'Content-Type': 'application/json',
           "Authorization": "Bearer " + localStorage.getItem("jwt")
        };
    }

    return await fetch(prefixed + endPoint, {
        method: httpMethod,
        headers: headers,
        body: httpMethod === 'GET' || httpMethod == 'DELETE' || httpMethod == 'HEAD' ? null : JSON.stringify(data)
    })
    .then(response => {
        // Check if response is successful (status code 200-299)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        if(response) {
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json"))
                return response.json(); // Parse JSON response
            return {};
        }
        else
            return {};
    })
    .then(items => {
        console.log('API response Items:', items);
        return items;
    })
    .catch(error => {
        console.error('Error fetching products:', error);
        window.location.href = "/auth/login";
    });
}


function makeRating(value, reviews = 0) {
  const fullStars = Math.floor(value);              // full ★
  const halfStar = value % 1 >= 0.5 ? 1 : 0;        // half ★
  const emptyStars = 5 - fullStars - halfStar;      // remaining ☆

  let starsHtml = "";

  // Add full stars
  for (let i = 0; i < fullStars; i++) {
    starsHtml += `<i class="fas fa-star"></i>`;
  }

  // Add half star if needed
  if (halfStar) {
    starsHtml += `<i class="fas fa-star-half-alt"></i>`;
  }

  // Add empty stars
  for (let i = 0; i < emptyStars; i++) {
    starsHtml += `<i class="far fa-star"></i>`;
  }

  return `
      ${starsHtml}
      ${value.toFixed(1)}${reviews ? ` (${reviews} reviews)` : ""}
  `;
}
