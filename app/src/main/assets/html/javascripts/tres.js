 

var container, raycaster;

var stap = false;

var mesh, mesh_hdd, mesh_ram, mesh_mb, mesh_cpu, mesh_lan;

var camera, controls, scene, renderer, effect, uniforms;
var lighting, ambient, keyLight, fillLight, backLight;

var windowHalfX = window.innerWidth / 2;
var windowHalfY = window.innerHeight / 2;

var mouse = new THREE.Vector2(), INTERSECTED;

var radius = 100;

 
var objs = ['awesome-phone.obj'];
var meshs = [mesh];

setTimeout(function(){
  animate();
}, 1000)

function init() {
 

}

function onWindowResize() {

   

}

function onKeyboardEvent(e) {

    if (e.code === 'KeyL') {

        lighting = !lighting;

        if (lighting) {

            ambient.intensity = 0.25;
            scene.add(keyLight);
            scene.add(fillLight);
            scene.add(backLight);

        } else {

            ambient.intensity = 1.0;
            scene.remove(keyLight);
            scene.remove(fillLight);
            scene.remove(backLight);

        }

    }
    if (e.code === 'KeyK') {
        camera.position.z = 500;
        camera.position.y = 200;
        meshs[3].position.y = 100;
        meshs[3].position.z = 200;
        stap = true;

    }
    if (e.code === 'KeyJ') {
        meshs[3].position.y = 0;
        meshs[3].position.z = 0;
        stap = false;

    }

}

function animate() {

    

}

function render() {
 

}


// const composer = new EffectComposer(renderer);
// composer.addPass(new RenderPass(new Scene(), new PerspectiveCamera()));
//
// const pass = new GlitchPass();
// pass.renderToScreen = true;
// composer.addPass(pass);
//
// const clock = new Clock();

$(function(){
  init();

  // requestAnimationFrame(render);
  // composer.render(clock.getDelta()); in render()

});

 
