## Android Sevice "GoMaWo" (GogiMasterWo(a)rrior)

> <span style="color:blue">인공지능 기반으로 육류의 익힘 정도와 부위 판별 가이드 라인을 모바일로 제시하기 위해 안드로이드를 선택했다.</span>



## App Flow

![app_flow_for_report](https://user-images.githubusercontent.com/50652715/106708494-51b4c280-6636-11eb-85e4-191177ea8366.png)



**각 View에 사용된 액티비티**

|            로딩            |                  촬영                  | 다시찍기 / 결과보기 |      결과      |     종료     |
| :------------------------: | :------------------------------------: | :-----------------: | :------------: | :----------: |
| Splashscreen Activity 사용 | MainActivity + CameraPreview(Activity) |                     | ResultActivity | ExitAcitvity |

* SurfacePreview Class를 사용해 카메라 프리뷰 기능을 제공하고 캡쳐한 사진이 마음에 들지 않을 경우를 고려해 다시찍는 버튼을 구성해 놓았다.

* 사용자로부터 촬영된 육류 이미지 데이터를 django 기반 서버로 전송(**<u>본 프로젝트에서는 aws 사용</u>**), 반환된 결과를 사용자에게 뿌려준다.



## Reference

* 홍 드로이드 카메라 만들기 : https://www.youtube.com/watch?v=MAB8LEfRIG8

  카메라 어플 만들기 : https://loveiskey.tistory.com/192

  좀더 깔끔한 예제 : http://blog.naver.com/PostView.nhn?blogId=whdals0&logNo=221408855795&categoryNo=29&parentCategoryNo=0&viewDate=&currentPage=1&postListTopCurrentPage=1&from=search

* 카메라 썸네일 : https://blog.naver.com/PostView.nhn?blogId=luku756&logNo=221127941797&parentCategoryNo=&categoryNo=31&viewDate=&isShowPopularPosts=true&from=search

* 완성도 있는 카메라 프리뷰 코드 : https://webnautes.tistory.com/822

* CAMERA_EASY : https://one-delay.tistory.com/40

* CAMERA2 : https://webnautes.tistory.com/822

* camera_preview : http://android-er.blogspot.com/2010/12/camera-preview-on-surfaceview.html
