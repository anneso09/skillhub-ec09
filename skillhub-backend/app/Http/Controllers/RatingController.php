<?php

namespace App\Http\Controllers;

use App\Models\Formation;
use App\Models\Rating;
use App\Models\Enrollment;
use Illuminate\Http\Request;

class RatingController extends Controller
{
    public function noter(Request $request, $id)
    {
        $utilisateurId = $request->auth_user_id;

        // Vérifier que la formation existe
        $formation = Formation::findOrFail($id);

        // Vérifier que l'apprenant est inscrit à la formation
        $inscrit = Enrollment::where('formation_id', $id)
                             ->where('utilisateur_id', $utilisateurId)
                             ->exists();

        if (!$inscrit) {
            return response()->json([
                'message' => 'Vous devez être inscrit à cette formation pour la noter.'
            ], 403);
        }

        // Valider la note (1 à 5)
        if (!isset($request->note) || $request->note < 1 || $request->note > 5) {
            return response()->json([
                'message' => 'La note doit être comprise entre 1 et 5.'
            ], 400);
        }

        // Vérifier que l'apprenant n'a pas déjà noté
        $dejaNote = Rating::where('formation_id', $id)
                          ->where('apprenant_id', $utilisateurId)
                          ->exists();

        if ($dejaNote) {
            return response()->json([
                'message' => 'Vous avez déjà noté cette formation.'
            ], 400);
        }

        // Créer le rating
        $rating = Rating::create([
            'formation_id' => $id,
            'apprenant_id' => $utilisateurId,
            'note'         => $request->note,
            'commentaire'  => $request->commentaire ?? null,
        ]);

        return response()->json($rating, 201);
    }
}