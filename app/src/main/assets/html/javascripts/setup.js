
/** * Number.prototype.format(n, x, s, c)
 *
 * @param integer n: length of decimal
 * @param integer x: length of whole part
 * @param mixed   s: sections delimiter
 * @param mixed   c: decimal delimiter
 */
Number.prototype.format = function(n, x, s, c) {
  var re = '\\d(?=(\\d{' + (x || 3) + '})+' + (n > 0 ? '\\D' : '$') + ')',
    num = this.toFixed(Math.max(0, ~~n));

  return (c ? num.replace('.', c) : num).replace(new RegExp(re, 'g'), '$&' + (s || ','));
};

// 12345678.9.format(2, 3, '.', ',');  // "12.345.678,90"
// 123456.789.format(4, 4, ' ', ':');  // "12 3456:7890"
// 12345678.9.format(0, 3, '-');

//touch click helper
(function ($) {
  $.fn.tclick = function(onhold) {
    this.bind("touchstart touchend touchmove", function (e) { onhold.call(this, e); e.stopPropagation(); e.preventDefault(); });
    this.bind("mousedown mouseup mousemove", function (e) { onhold.call(this, e); });
    return this;
  };
})(jQuery);


var timeRemaining = 1000;
var tick, mouseX, mouseY;
var wrapper = $(document);
var helm = $('#helm');
var initTop = 0;
var currentTop = initTop;
var lastDirection;
var allHandsOnDeck = false;

$(function(){


    $('.popover .close').click(function(){
      $('.popover').toggleClass("show");
    });

    $('.btn-reminder').click(function(){
      $('#reminder').toggleClass("show");
    });

    $('#reminder .close').click(function(){
      $('#reminder').toggleClass("show");
    });

  // wrapper.tclick(function(e){
  //   if (e.originalEvent.type == "mousedown" || e.originalEvent.type == "touchstart" && timeRemaining > 0 && e.originalEvent.target.nodeName != 'A') {
  //
  //     $('#master').addClass('on-hold');
  //     // console.log(e.originalEvent);
  //     console.log(e.originalEvent.target.nodeName);
  //
  //     // mouseX = Math.round(e.pageX) || e.originalEvent.touches[0].pageX;
  //     // mouseY = Math.round(e.pageY) || e.originalEvent.touches[0].pageY;
  //
  //
  //     // $('#helm').css({
  //     //   left: mouseX - 20,
  //     //   top: mouseY - 20
  //     // }).addClass('turn');
  //
  //     tick = setInterval(function(e){
  //       console.log(timeRemaining/1000);
  //
  //       if (timeRemaining == 0) {
  //         console.log("BOOM!");
  //
  //         // Run SVG animation of the Helm
  //         $('#deck').addClass('all-hands-on');
  //         $('#helm').removeClass('turn');
  //
  //
  //         timeRemaining = 1000;
  //         clearInterval(tick);
  //       }
  //       timeRemaining -= 1000;
  //
  //     }, 400);
  //
  //   } else if (e.originalEvent.type == "mouseup"  || e.originalEvent.type == "touchend") {
  //     console.log('up');
  //     timeRemaining = 1000;
  //     clearInterval(tick);
  //
  //     $('#helm').removeClass('turn');
  //     $('#deck').removeClass('all-hands-on');
  //     $('#master').removeClass('on-hold');
  //
  //   }
  //
  //   if (allHandsOnDeck && e.originalEvent.type == "mousemove" || e.originalEvent.type == "touchmove") {
  //     currentTop = Math.min(e.pageY);
  //
  //     // console.log(currentTop);
  //
  //     if (initTop < currentTop) {
  //       initTop = currentTop;
  //       if (lastDirection != 'paaajo') {
  //         console.log('pabajo?');
  //       }
  //
  //       lastDirection = 'paaajo';
  //     } else {
  //       initTop = currentTop;
  //       if (lastDirection != 'parrria') {
  //         console.log('parrria?');
  //       }
  //       lastDirection = 'parrria';
  //     }
  //
  //   }
  //
  //   // if (e.originalEvent.type == "mousemove") {
  //   //   //  helm.offset().left, helm.offset().top
  //   //   mouseX = Math.min(e.pageX);
  //   //   mouseY = Math.min(e.pageY);
  //   //   // console.log(mouseX, mouseY);
  //   //   // console.log($(document).width(), $(document).height());
  //   //
  //   //
  //   //   // if (mouseX < 0) { mouseX = 0; }
  //   //   // if (mouseY < 0) { mouseY = 0; }
  //   // }
  //
  // });

  // $('.revealer li a', this).click(function(){
  //
  //   var tween = new TimelineLite();
  //   var items = $(this).next().children().children();
  //
  //   if ($(this).hasClass('content-closed')) {
  //     $(this).removeClass('content-closed');
  //     tween.staggerTo(items, 0.5, {
  //         opacity: 1,
  //         left: 0,
  //         ease: Cubic.easeInOut
  //       },
  //     0.2);
  //   } else {
  //     $(this).addClass('content-closed');
  //     tween.staggerTo(items, 0.5, {
  //         opacity: 0,
  //         left: -32,
  //         ease: Cubic.easeInOut
  //       },
  //     0.2);
  //   }
  //
  // });


  // Animations

  // Fades
  // TweenMax.staggerTo('.fadeInDown', 1, {
  //   opacity: 1,
  //   top: 0,
  //   ease: Cubic.easeOut,
  //   delay: 0.5
  // }, 0.2);
  //
  // TweenMax.staggerTo('.fadeInUp', 1, {
  //   opacity: 1,
  //   bottom: 0,
  //   ease: Cubic.easeOut,
  //   delay: 0.5
  // }, 0.2);

  // // Counter
  // var counter = { var: 0 };
  //
  // TweenMax.to(counter, 5, {
  //   var: 10000,
  //   onUpdate: function () {
  //     $('.texta').text(counter.var.format(2, 3, '.', ','));
  //   },
  //   ease:Circ.easeOut
  // });

  // String.prototype.wrap = function (method) {
  //   return this.replace({
  //       chars: /[^a-z0-9]/g,
  //       words: /\w+/g,
  //       lines: /.+$/gm
  //   }[method || "words"], function ($1) {
  //       return "<span>" + $1 + "</span>";
  //   });
  // };
  //
  // var p = $('.texta')[0];
  // p.innerHTML = p.innerText.wrap('chars');
  //
  // var words = document.querySelectorAll('span');
  //
  // var tl = new TimelineMax();
  // tl.staggerFrom(words, 0.5, { opacity:0, ease: Linear.ease, onStart: function(){
  //   $('.perspective p').addClass('active');
  // }}, 0.05, "#start");
  // tl.staggerFrom(words, 0.5, {y: 20, ease: Linear.ease}, 0.5, "#start");
  // tl.staggerFrom(words, 0.5, {translateY: -90, rotationZ: 70, transformOrigin:"0 50%", ease: Quart.easeOut}, 0.5, "#start+=0.1");
  window.mySwiper = new Swiper ('.swiper-container', {
    loop: true,
    parallax: true,
    pagination: {
      el: '.swiper-pagination',
    },
    simulateTouch: true,
    mousewheel: {
      invert: true,
    }
  });

  //
  // $(".tabvideo").on("click", function(event){
  //   event.preventDefault();
  //   $(".iphoneBack").prepend('<div class="video-modal"><div class="video"></div></div>');
  //   setTimeout(function(){
  //     $(".video").addClass("videoActive");
  //   },10);
  // });
  //
  // $(".iphoneBack").on("click",".video", function(event){
  //   event.preventDefault();
  //   $(".video").removeClass("videoActive");
  //   setTimeout(function(){
  //     $(".video-modal").remove();
  //   },400);
  // })

});
