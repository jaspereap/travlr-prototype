// function initMap() {
//     const map = new google.maps.Map(document.getElementById("map"), {
//       center: { lat: 1.3521, lng: 103.8198 },
//       zoom: 11,
//       mapTypeId: "terrain",
//     });
  
//     map.setTilt(0);
//   }
  
//   window.initMap = initMap;



// Initialize and add the map
let map;

async function initMap() {
  // The location of Uluru
  const position = { lat: 1.3521, lng: 103.8198 };
  // Request needed libraries.
  //@ts-ignore
  const { Map } = await google.maps.importLibrary("maps");
  const { AdvancedMarkerElement } = await google.maps.importLibrary("marker");

  // The map, centered at Uluru
  map = new Map(document.getElementById("map"), {
    zoom: 11,
    center: position,
    mapId: "DEMO_MAP_ID",
    streetViewControl: false,
    zoomControl: true,
    zoomControlOptions: {
      position: google.maps.ControlPosition.LEFT_CENTER,
    },
  });

  // The marker, positioned at Uluru
  const marker = new AdvancedMarkerElement({
    map: map,
    position: position,
    title: "Uluru",
  });
}

initMap();