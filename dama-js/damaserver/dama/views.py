from django.shortcuts import render, get_object_or_404
from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from .models import Move, Player


def index(request, uniqueId):
    player = None
    is_starter = 1
    try:
        player = Player.objects.get(unique_id=uniqueId, is_new=True)
        player.is_new = False
        player.save()
        is_starter = 0
    except ObjectDoesNotExist:
        player = Player(unique_id=uniqueId, is_new=True)
        player.save()
    return render(request, 'dama/dama.html', {'isStarter':is_starter})

def send(request):
    post_data = request.POST
    move = Move(
        unique_id=int(post_data['unique_id'].strip()),
        is_read = False,
        is_top = int(post_data['is_top'].strip()) is 1,
        is_pending = int(post_data['is_pending'].strip()) is 1,
        start_row = int(post_data['start_row'].strip()),
        start_col = int(post_data['start_col'].strip()),
        landing_row = int(post_data['landing_row'].strip()),
        landing_col = int(post_data['landing_col'].strip())
    )
    move.save()
    return HttpResponse("SUCCESS")

def recieve(request):
    post_data = request.POST
    move = get_object_or_404(
        Move,
        unique_id=int(post_data['unique_id'].strip()),
        is_read = False,
        is_top = int(post_data['is_top'].strip()) is 1
    )
    json_template = '[[ %i, %i], [ %i, %i]], []' \
                    if move.is_pending \
                    else '[[ %i, %i], [ %i, %i]]'
    response = json_template % (
        move.start_row,
        move.start_col,
        move.landing_row,
        move.landing_col
    )
    move.is_read = True
    move.save()
    return HttpResponse(response)

